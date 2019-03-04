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

package com.botscrew.botframework.plivo.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Implementation of {@link ExecutorProperties} for describing messenger sender executor
 * Contains default values for the properties.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "plivo.bot.sender-executor")
public class SenderTaskExecutorProperties implements ExecutorProperties {
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;
    private Integer keepAliveSeconds;

    public SenderTaskExecutorProperties() {
        this.corePoolSize = 5;
        this.maxPoolSize = 25;
        this.queueCapacity = 500;
        this.keepAliveSeconds = 10;
    }
}
