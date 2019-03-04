/*
 * Copyright 2019 BotsCrew
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.botscrew.botframework.plivo.service.impl;

import com.botscrew.botframework.plivo.config.property.PlivoProperties;
import com.botscrew.botframework.plivo.constant.ApiMethod;
import com.botscrew.botframework.plivo.domain.action.AfterSendMessage;
import com.botscrew.botframework.plivo.domain.action.BeforeSendMessage;
import com.botscrew.botframework.plivo.domain.internal.LockingQueue;
import com.botscrew.botframework.plivo.exception.InterceptorInterruptedException;
import com.botscrew.botframework.plivo.model.PlivoBot;
import com.botscrew.botframework.plivo.model.PlivoMessage;
import com.botscrew.botframework.plivo.model.PlivoUser;
import com.botscrew.botframework.plivo.model.outgoing.PlivoMessageSentResponse;
import com.botscrew.botframework.plivo.model.outgoing.PlivoOutgoingRequest;
import com.botscrew.botframework.plivo.service.InterceptorsTrigger;
import com.botscrew.botframework.plivo.service.PlivoTokenizedSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.botscrew.botframework.plivo.util.PlivoHttpHeadersCreator.createHeaders;

@Slf4j
public class PlivoTokenizedSenderImpl implements PlivoTokenizedSender {

    private final RestTemplate restTemplate;
    private final PlivoProperties properties;
    private final InterceptorsTrigger interceptorsTrigger;
    private final TaskExecutor taskExecutor;
    private final Map<Long, LockingQueue<PlivoOutgoingRequest>> lockingRequests;

    public PlivoTokenizedSenderImpl(RestTemplate restTemplate, PlivoProperties properties,
                                    InterceptorsTrigger interceptorsTrigger, TaskExecutor taskExecutor) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.interceptorsTrigger = interceptorsTrigger;
        this.lockingRequests = new ConcurrentHashMap<>();
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void send(PlivoBot plivoBot, PlivoMessage plivoMessage) {
        send(plivoBot.getAuthId(), plivoBot.getAuthToken(), plivoBot.getPhoneNumber(), plivoMessage.getUser(),
                plivoMessage.getText());
    }

    @Override
    public void send(String authId, String authToken, Long botPhoneNumber, PlivoMessage plivoMessage) {
        send(authId, authToken, botPhoneNumber, plivoMessage.getUser(), plivoMessage.getText());
    }

    @Override
    public void send(String authId, String authToken, Long botPhoneNumber, PlivoUser plivoUser, String text) {

        try {
            triggerBeforeMessageInterceptors(plivoUser, text);
        } catch (InterceptorInterruptedException e) {
            log.info(e.getMessage());
            return;
        }

        log.debug(String.format("Posting message: \"%s\" to user %d", text, plivoUser.getPhoneNumber()));
        PlivoOutgoingRequest request = convertToOutgoingRequest(plivoUser, text, botPhoneNumber);
        Long id = plivoUser.getPhoneNumber();
        LockingQueue<PlivoOutgoingRequest> queue = lockingRequests.computeIfAbsent(id, k -> new LockingQueue<>());
        queue.push(request);
        if (!queue.isLocked()) startSendRequests(authId, authToken, plivoUser, queue);
    }

    private void startSendRequests(String authId, String authToken, PlivoUser plivoUser,
                                   LockingQueue<PlivoOutgoingRequest> lockingQueue) {
        taskExecutor.execute(() -> {
            if (lockingQueue.tryLock()) {
                while (true) {
                    Optional<PlivoOutgoingRequest> requestOpt = lockingQueue.getNextOrUnlock();
                    if (!requestOpt.isPresent()) break;

                    PlivoOutgoingRequest top = requestOpt.get();
                    sendRequest(authId, authToken, plivoUser, top);
                }
            }
        });
    }

    private void sendRequest(String authId, String authToken, PlivoUser plivoUser,
                             PlivoOutgoingRequest request) {
        String url = properties.makeRequestUrl(authId, ApiMethod.MESSAGE);
        HttpEntity<PlivoOutgoingRequest> httpEntity = new HttpEntity<>(request,
                createHeaders(authId, authToken));

        ResponseEntity<PlivoMessageSentResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity,
                PlivoMessageSentResponse.class);

        try {
            triggerAfterMessageInterceptors(plivoUser, request.getText(), responseEntity.getBody());
        } catch (InterceptorInterruptedException e) {
            log.info(e.getMessage());
        }
    }

    private PlivoOutgoingRequest convertToOutgoingRequest(PlivoUser plivoUser, String text, Long botPhoneNumber) {
        return PlivoOutgoingRequest.builder()
                .src(botPhoneNumber)
                .dst(plivoUser.getPhoneNumber())
                .text(text)
                .build();
    }

    private void triggerBeforeMessageInterceptors(PlivoUser plivoUser, String text) {
        BeforeSendMessage beforeSendMessage = new BeforeSendMessage(plivoUser, text);
        interceptorsTrigger.trigger(beforeSendMessage);
    }

    private void triggerAfterMessageInterceptors(PlivoUser plivoUser, String text, PlivoMessageSentResponse response) {
        AfterSendMessage afterSendMessage = new AfterSendMessage(plivoUser, text, response);
        interceptorsTrigger.trigger(afterSendMessage);
    }
}
