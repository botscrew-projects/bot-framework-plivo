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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Describes Plivo-specific properties used inside Plivo module
 */
@Data
@ConfigurationProperties(prefix = "plivo.bot")
public class PlivoProperties {

    private String authId;
    private String authToken;
    private Long phoneNumber;
    private String applicationId;
    private String baseUrl;
    private String baseApiRequestUrl = "https://api.plivo.com/v1/Account/";
    private String eventsPath = "/plivo/events";

    public String makeRequestUrl(String methodName) {
        return this.baseApiRequestUrl + this.authId + "/" + methodName + "/";
    }

    public String makeRequestUrl(String authId, String methodName) {
        return this.baseApiRequestUrl + authId + "/" + methodName + "/";
    }

}
