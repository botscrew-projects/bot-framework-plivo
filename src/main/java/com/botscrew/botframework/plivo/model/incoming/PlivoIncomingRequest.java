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