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

import com.botscrew.botframework.plivo.service.WebhookRegistrator;
import com.botscrew.botframework.plivo.service.WebhookRegistratorRunner;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

public class WebhookRegistratorRunnerImpl implements WebhookRegistratorRunner {

    private WebhookRegistrator webhookRegistrator;

    public WebhookRegistratorRunnerImpl(WebhookRegistrator webhookRegistrator) {
        this.webhookRegistrator = webhookRegistrator;
    }

    @PostConstruct
    @Override
    public void init() {
        webhookRegistrator.registerDefaultWebhook();
    }
}
