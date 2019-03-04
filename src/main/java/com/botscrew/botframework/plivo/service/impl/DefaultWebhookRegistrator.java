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
import com.botscrew.botframework.plivo.model.outgoing.WebHook;
import com.botscrew.botframework.plivo.service.Plivo;
import com.botscrew.botframework.plivo.service.WebhookRegistrator;

public class DefaultWebhookRegistrator implements WebhookRegistrator {

    private final PlivoProperties properties;
    private final Plivo plivo;

    public DefaultWebhookRegistrator(PlivoProperties properties, Plivo plivo) {
        this.properties = properties;
        this.plivo = plivo;
    }

    @Override
    public void registerDefaultWebhook() {
        String baseUrl = properties.getBaseUrl();
        String eventsPath = properties.getEventsPath();
        WebHook webhook = new WebHook();
        webhook.setMessageUrl(baseUrl + eventsPath);
        plivo.setWebHook(properties.getAuthId(), properties.getAuthToken(), properties.getApplicationId(), webhook);
    }
}
