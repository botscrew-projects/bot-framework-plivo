package com.botscrew.botframework.plivo.model.outgoing;

import com.botscrew.botframework.sender.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlivoOutgoingRequest implements Message {
    @JsonProperty("src")
    private Long src;
    @JsonProperty("dst")
    private Long dst;
    private String text;
}
