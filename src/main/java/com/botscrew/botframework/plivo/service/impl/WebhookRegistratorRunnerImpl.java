package com.botscrew.botframework.plivo.service.impl;

import com.botscrew.botframework.plivo.service.WebhookRegistrator;
import com.botscrew.botframework.plivo.service.WebhookRegistratorRunner;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@AllArgsConstructor
public class WebhookRegistratorRunnerImpl implements WebhookRegistratorRunner {

    @Autowired
    private WebhookRegistrator webhookRegistrator;

    @PostConstruct
    @Override
    public void init() {
        webhookRegistrator.registerDefaultWebhook();
    }
}
