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

import com.botscrew.botframework.plivo.domain.Interruption;
import com.botscrew.botframework.plivo.domain.PlivoInterceptor;
import com.botscrew.botframework.plivo.domain.action.AfterSendMessage;
import com.botscrew.botframework.plivo.domain.action.BeforeSendMessage;
import com.botscrew.botframework.plivo.domain.action.GetEvent;
import com.botscrew.botframework.plivo.domain.action.ProcessedEvent;
import com.botscrew.botframework.plivo.exception.InterceptorInterruptedException;
import com.botscrew.botframework.plivo.service.InterceptorsTrigger;

import java.util.List;

public class DefaultInterceptorsTrigger implements InterceptorsTrigger {

    private final List<PlivoInterceptor<GetEvent>> getEventInterceptors;
    private final List<PlivoInterceptor<ProcessedEvent>> processedEventInterceptors;
    private final List<PlivoInterceptor<BeforeSendMessage>> beforeSendMessageInterceptors;
    private final List<PlivoInterceptor<AfterSendMessage>> afterSendMessageInterceptors;

    public DefaultInterceptorsTrigger(List<PlivoInterceptor<GetEvent>> getEventInterceptors, List<PlivoInterceptor<ProcessedEvent>> processedEventInterceptors, List<PlivoInterceptor<BeforeSendMessage>> beforeSendMessageInterceptors, List<PlivoInterceptor<AfterSendMessage>> afterSendMessageInterceptors) {
        this.getEventInterceptors = getEventInterceptors;
        this.processedEventInterceptors = processedEventInterceptors;
        this.beforeSendMessageInterceptors = beforeSendMessageInterceptors;
        this.afterSendMessageInterceptors = afterSendMessageInterceptors;
    }


    @Override
    public void trigger(GetEvent getEvent) {
        for (PlivoInterceptor<GetEvent> interceptor : getEventInterceptors) {
            Interruption interruption = interceptor.onAction(getEvent);
            check(interruption);
        }
    }

    @Override
    public void trigger(ProcessedEvent processedEvent) {
        for (PlivoInterceptor<ProcessedEvent> interceptor : processedEventInterceptors) {
            Interruption interruption = interceptor.onAction(processedEvent);
            check(interruption);
        }
    }

    @Override
    public void trigger(BeforeSendMessage beforeSendMessage) {
        for (PlivoInterceptor<BeforeSendMessage> interceptor : beforeSendMessageInterceptors) {
            Interruption interruption = interceptor.onAction(beforeSendMessage);
            check(interruption);
        }
    }

    @Override
    public void trigger(AfterSendMessage afterSendMessage) {
        for (PlivoInterceptor<AfterSendMessage> interceptor : afterSendMessageInterceptors) {
            Interruption interruption = interceptor.onAction(afterSendMessage);
            check(interruption);
        }
    }

    private void check(Interruption interruption) {
        if (interruption.isInterrupt()) {
            String message = "Interceptor interrupted execution with reason: " + interruption.getCause();
            throw new InterceptorInterruptedException(message);
        }
    }
}
