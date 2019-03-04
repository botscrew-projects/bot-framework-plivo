package com.botscrew.botframework.plivo.model.outgoing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlivoMessageSentResponse {
    private String message;
    @JsonProperty("message_uuid")
    private String[] messageUuid;
    @JsonProperty("api_id")
    private String apiId;

}
