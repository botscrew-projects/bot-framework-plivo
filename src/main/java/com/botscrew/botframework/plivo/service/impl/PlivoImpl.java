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
import com.botscrew.botframework.plivo.exception.PlivoCDKException;
import com.botscrew.botframework.plivo.model.incoming.PlivoResponse;
import com.botscrew.botframework.plivo.model.outgoing.WebHook;
import com.botscrew.botframework.plivo.service.Plivo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static com.botscrew.botframework.plivo.util.PlivoHttpHeadersCreator.createHeaders;

@Slf4j
public class PlivoImpl implements Plivo {
    private final RestTemplate restTemplate;
    private final PlivoProperties properties;

    public PlivoImpl(RestTemplate restTemplate, PlivoProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public void setWebHook(String authId, String authToken, String applicationId, WebHook webHook) {
        try {
            String requestUrl = properties.makeRequestUrl(ApiMethod.APPLICATION) + applicationId + "/";
            HttpEntity<WebHook> entity = new HttpEntity<>(webHook, createHeaders(authId, authToken));
            PlivoResponse response = restTemplate.postForObject(requestUrl, entity, PlivoResponse.class);
            isSuccess(response);
        } catch (HttpClientErrorException e) {
            log.info("Webhook setup has been failed: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Webhook setup has been failed: " + e.getMessage(), e);
        }
    }

    private void isSuccess(PlivoResponse plivoResponse) {
        if(plivoResponse.getError() != null) throw new PlivoCDKException();
    }
}