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

package com.botscrew.botframework.plivo.model;

public class DefaultPlivoBot implements PlivoBot {
    private String authId;
    private String authToken;
    private Long phoneNumber;

    public DefaultPlivoBot(Long phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public DefaultPlivoBot(String authId, String authToken, Long phoneNumber) {
        this.authId = authId;
        this.authToken = authToken;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getAuthId() {
        return authId;
    }

    @Override
    public String getAuthToken() {
        return authToken;
    }

    @Override
    public Long getPhoneNumber() {
        return phoneNumber;
    }
}