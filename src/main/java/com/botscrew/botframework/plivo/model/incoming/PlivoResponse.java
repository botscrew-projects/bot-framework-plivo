package com.botscrew.botframework.plivo.model.incoming;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlivoResponse {
    @JsonProperty("api_id")
    private String apiId;
    private String error;
    private String message;
}
