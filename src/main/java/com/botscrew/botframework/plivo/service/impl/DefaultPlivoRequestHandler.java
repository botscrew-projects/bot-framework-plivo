/*
 * Copyright 2018 BotsCrew
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

import com.botscrew.botframework.container.TextContainer;
import com.botscrew.botframework.domain.argument.ArgumentType;
import com.botscrew.botframework.domain.argument.kit.SimpleArgumentKit;
import com.botscrew.botframework.domain.argument.wrapper.SimpleArgumentWrapper;
import com.botscrew.botframework.plivo.domain.action.GetEvent;
import com.botscrew.botframework.plivo.domain.action.ProcessedEvent;
import com.botscrew.botframework.plivo.exception.InterceptorInterruptedException;
import com.botscrew.botframework.plivo.exception.PlivoCDKException;
import com.botscrew.botframework.plivo.model.PlivoBot;
import com.botscrew.botframework.plivo.model.PlivoUser;
import com.botscrew.botframework.plivo.model.incoming.PlivoIncomingRequest;
import com.botscrew.botframework.plivo.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;

@Slf4j
public class DefaultPlivoRequestHandler implements PlivoRequestHandler {
    private final PlivoBotProvider plivoBotProvider;
    private final PlivoUserProvider plivoUserProvider;
    private final InterceptorsTrigger interceptorsTrigger;
    private final ExceptionHandler exceptionHandler;
    private final TextContainer textContainer;

    public DefaultPlivoRequestHandler(PlivoBotProvider plivoBotProvider,
                                      PlivoUserProvider plivoUserProvider,
                                      InterceptorsTrigger interceptorsTrigger,
                                      ExceptionHandler exceptionHandler, TextContainer textContainer) {
        this.plivoBotProvider = plivoBotProvider;
        this.plivoUserProvider = plivoUserProvider;
        this.interceptorsTrigger = interceptorsTrigger;
        this.exceptionHandler = exceptionHandler;
        this.textContainer = textContainer;
    }

    @Override
    public void handle(PlivoIncomingRequest plivoIncomingRequest) {
        try {
            log.debug("Plivo request: {}", plivoIncomingRequest);
            handleRequest(plivoIncomingRequest);
        } catch (Exception e) {
            boolean handled = exceptionHandler.handle(e);
            if (!handled) throw e;
        }
    }

    private void handleRequest(PlivoIncomingRequest plivoIncomingRequest) {
        PlivoBot plivoBot = plivoBotProvider.getByPhoneNumber(plivoIncomingRequest.getTo());

        if (plivoBot == null)
            throw new PlivoCDKException("Plivo Bot Provider returns NULL for phone number: "
                    + plivoIncomingRequest.getTo());

        PlivoUser user = plivoUserProvider.getByUserPhoneNumberAndPlivoBot(plivoIncomingRequest.getFrom(), plivoBot);

        try {
            triggerGetEventInterceptors(plivoIncomingRequest, user);
        } catch (InterceptorInterruptedException e) {
            log.info(e.getMessage());
            return;
        }

        handleText(user, plivoIncomingRequest);

        try {
            triggerProcessedEventInterceptors(plivoIncomingRequest, user);
        } catch (InterceptorInterruptedException e) {
            log.info(e.getMessage());
        }
    }

    // Wrap PlivoUser and SMS text and process with Bot Framework
    private void handleText(PlivoUser plivoUser, PlivoIncomingRequest plivoIncomingRequest) {
        SimpleArgumentKit kit = new SimpleArgumentKit();
        kit.put(ArgumentType.TEXT, new SimpleArgumentWrapper(plivoIncomingRequest.getText()));
        textContainer.process(plivoUser, kit);
    }

    private void triggerGetEventInterceptors(PlivoIncomingRequest plivoIncomingRequest, PlivoUser user) {
        GetEvent getEvent = new GetEvent(plivoIncomingRequest, user);
        interceptorsTrigger.trigger(getEvent);
    }

    private void triggerProcessedEventInterceptors(PlivoIncomingRequest plivoIncomingRequest, PlivoUser user) {
        ProcessedEvent processedEvent = new ProcessedEvent(plivoIncomingRequest, user);
        interceptorsTrigger.trigger(processedEvent);
    }

}
