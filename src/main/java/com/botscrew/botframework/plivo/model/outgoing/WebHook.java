package com.botscrew.botframework.plivo.model.outgoing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebHook {
    @JsonProperty("message_url")
    private String messageUrl;
}
