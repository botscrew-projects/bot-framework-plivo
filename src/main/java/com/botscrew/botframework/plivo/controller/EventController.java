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

package com.botscrew.botframework.plivo.controller;

import com.botscrew.botframework.plivo.model.incoming.PlivoIncomingRequest;
import com.botscrew.botframework.plivo.service.PlivoRequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("${plivo.bot.events-path:/plivo/events}")
public class EventController {
    private final ObjectMapper objectMapper;
    private final PlivoRequestHandler plivoRequestHandler;

    @RequestMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity receive(@RequestParam Map<String, String> requestMap) {
        plivoRequestHandler.handle(objectMapper.convertValue(requestMap, PlivoIncomingRequest.class));
        return ResponseEntity.ok().build();
    }
}
