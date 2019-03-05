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

package com.botscrew.botframework.plivo.service;

import com.botscrew.botframework.plivo.model.PlivoBot;
import com.botscrew.botframework.plivo.model.PlivoMessage;
import com.botscrew.botframework.plivo.model.PlivoUser;
import com.botscrew.botframework.sender.Sender;

import java.util.concurrent.ScheduledFuture;

public interface PlivoTokenizedSender extends Sender<PlivoBot, PlivoMessage> {
    void send(String authId, String authToken, Long botPhoneNumber, PlivoMessage plivoMessage);

    void send(String authId, String authToken, Long botPhoneNumber, PlivoUser plivoUser, String text);

    ScheduledFuture send(String authId, String authToken, Long botPhoneNumber, PlivoUser plivoUser, String text, int delay);
}
