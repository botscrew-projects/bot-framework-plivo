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

package com.botscrew.botframework.plivo.config;

import com.botscrew.botframework.container.TextContainer;
import com.botscrew.botframework.domain.user.Platform;
import com.botscrew.botframework.plivo.config.property.PlivoProperties;
import com.botscrew.botframework.plivo.config.property.SenderTaskExecutorProperties;
import com.botscrew.botframework.plivo.controller.EventController;
import com.botscrew.botframework.plivo.domain.PlivoInterceptor;
import com.botscrew.botframework.plivo.domain.action.AfterSendMessage;
import com.botscrew.botframework.plivo.domain.action.BeforeSendMessage;
import com.botscrew.botframework.plivo.domain.action.GetEvent;
import com.botscrew.botframework.plivo.domain.action.ProcessedEvent;
import com.botscrew.botframework.plivo.service.*;
import com.botscrew.botframework.plivo.service.impl.*;
import com.botscrew.botframework.sender.PlatformSender;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Spring configuration for Plivo module
 */
@Configuration
@EnableConfigurationProperties(value = {
        PlivoProperties.class,
        SenderTaskExecutorProperties.class
})
@EnableAsync
public class PlivoCDKConfiguration {

    @Bean
    @ConditionalOnMissingBean(Plivo.class)
    public Plivo plivo(RestTemplate restTemplate, PlivoProperties properties) {
        return new PlivoImpl(restTemplate, properties);
    }

    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate defaultRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean(EventController.class)
    public EventController plivoEventController(ObjectMapper objectMapper, PlivoRequestHandler plivoRequestHandler) {
        return new EventController(objectMapper, plivoRequestHandler);
    }

    @Bean
    @ConditionalOnMissingBean(PlivoRequestHandler.class)
    public PlivoRequestHandler defaultPlivoRequestHandler(PlivoBotProvider plivoBotProvider,
                                                          PlivoUserProvider plivoUserProvider,
                                                          InterceptorsTrigger interceptorsTrigger,
                                                          ExceptionHandler exceptionHandler,
                                                          TextContainer textContainer) {
        return new DefaultPlivoRequestHandler(plivoBotProvider, plivoUserProvider, interceptorsTrigger,
                exceptionHandler, textContainer);
    }

    @Bean
    @ConditionalOnMissingBean(PlivoBotProvider.class)
    public PlivoBotProvider defaultPlivoBotProvider() {
        return new DefaultPlivoBotProvider();
    }

    @Bean
    @ConditionalOnMissingBean(PlivoTokenizedSender.class)
    public PlivoTokenizedSender plivoTokenizedSender(RestTemplate restTemplate, PlivoProperties properties,
                                                     InterceptorsTrigger interceptorsTrigger,
                                                     PlatformSender platformSender,
                                                     @Qualifier("plivoTokenizedSenderTaskExecutor") TaskExecutor
                                                                 taskExecutor,
                                                     @Qualifier("defaultPlivoSenderTaskScheduler") ThreadPoolTaskScheduler
                                                                 scheduler) {
        PlivoTokenizedSender plivoTokenizedSender = new PlivoTokenizedSenderImpl(restTemplate, properties,
                interceptorsTrigger, taskExecutor, scheduler);
        platformSender.addSender(Platform.APP, plivoTokenizedSender);
        return plivoTokenizedSender;
    }

    @Bean
    @ConditionalOnProperty({"plivo.bot.auth-id", "plivo.bot.auth-token", "plivo.bot.phone-number"})
    public PlivoSender defaultPlivoSender(PlivoTokenizedSender plivoTokenizedSender, PlivoProperties properties) {
        return new DefaultPlivoSender(plivoTokenizedSender, properties);
    }

    @Bean
    @ConditionalOnProperty({"plivo.bot.auth-id", "plivo.bot.auth-token", "plivo.bot.phone-number"})
    @ConditionalOnMissingBean(WebhookRegistrator.class)
    public WebhookRegistrator defaultPlivoWebhookRegistrator(PlivoProperties properties, Plivo plivo) {
        return new DefaultWebhookRegistrator(properties, plivo);
    }

    @Bean
    @ConditionalOnMissingBean(WebhookRegistrator.class)
    @ConditionalOnProperty({"plivo.bot.auth-id", "plivo.bot.auth-token", "plivo.bot.phone-number"})
    public WebhookRegistratorRunner defaultPlivoWebhookRegistratorRunner(WebhookRegistrator webhookRegistrator) {
        return new WebhookRegistratorRunnerImpl(webhookRegistrator);
    }

    @Bean
    @ConditionalOnMissingBean(value = ObjectMapper.class)
    public ObjectMapper defaultJacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean(value = PlivoUserProvider.class)
    public PlivoUserProvider defaultPlivoUserProvider() {
        return new DefaultUserProvider();
    }

    @Bean
    @ConditionalOnMissingBean(value = ExceptionHandler.class)
    public ExceptionHandler defaultPlivoExceptionHandler() {
        return new DefaultExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(value = InterceptorsTrigger.class)
    public InterceptorsTrigger defaultPlivoInterceptorsTrigger(
            @Autowired(required = false) List<PlivoInterceptor<GetEvent>> getEventInterceptors,
            @Autowired(required = false) List<PlivoInterceptor<ProcessedEvent>> processedEventInterceptors,
            @Autowired(required = false) List<PlivoInterceptor<BeforeSendMessage>> beforeSendMessageInterceptors,
            @Autowired(required = false) List<PlivoInterceptor<AfterSendMessage>> afterSendMessageInterceptors) {
        return new DefaultInterceptorsTrigger(getThisOrEmptyIfNull(getEventInterceptors),
                getThisOrEmptyIfNull(processedEventInterceptors), getThisOrEmptyIfNull(beforeSendMessageInterceptors),
                getThisOrEmptyIfNull(afterSendMessageInterceptors));
    }

    private List getThisOrEmptyIfNull(List list) {
        if (list != null) {
            return list;
        } else return new ArrayList();
    }

    @Bean(name = "plivoTokenizedSenderTaskExecutor")
    public TaskExecutor messageSendingTaskExecutor(SenderTaskExecutorProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        executor.initialize();
        return executor;
    }

    @Bean(name = "defaultPlivoSenderTaskScheduler")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.initialize();
        return scheduler;
    }

}
