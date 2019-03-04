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

package com.botscrew.botframework.plivo.model.incoming;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlivoIncomingRequest {
    @JsonProperty("MessageUUID")
    private String messageUUID;
    @JsonProperty("From")
    private Long from;
    @JsonProperty("To")
    private Long to;
    @JsonProperty("Text")
    private String text;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Units")
    private Integer units;
    @JsonProperty("TotalRate")
    private Integer totalRate;
    @JsonProperty("TotalAmount")
    private Integer totalAmount;
    @JsonProperty("MessageIntent")
    private String messageIntent;
}