package com.botscrew.botframework.plivo.model;

import com.botscrew.botframework.sender.Message;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlivoMessage implements Message {
    private PlivoUser user;
    private String text;
}
