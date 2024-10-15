package com.hcc.socket.chat.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String roomId;
    private String type;
    private String text;


}
