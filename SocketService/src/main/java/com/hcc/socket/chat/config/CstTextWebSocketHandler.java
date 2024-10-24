package com.hcc.socket.chat.config;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcc.socket.chat.SessionContext;
import com.hcc.socket.chat.model.Message;
import com.hcc.socket.security.jwt.UserData;
import com.hcc.socket.chat.model.User;
import com.hcc.socket.chat.infra.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Getter
@Slf4j
@Component
@RequiredArgsConstructor
public class CstTextWebSocketHandler extends TextWebSocketHandler {

    private final UserRepository userRepository;
    private final SessionContext sessionContext;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UserData userData = (UserData) session.getAttributes().get("userFromJWT");
        User user = User.builder()
                .id(userData.getId())
                .session(session)
                .connServer(null)
                .build();
        log.info("conn_user : {}", user);
        User managedUser = userRepository.save(user);

        sessionContext.putUser(userData.getId(), session);


    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        Message msg = messageParse(message);
        String roomId = msg.getRoomId();
        log.info("message handling - roomId : {}", roomId);
        List<WebSocketSession> sessions = sessionContext.getSessionsByRoomId(roomId);
        for (WebSocketSession receiverSession : sessions) {
            try {
                receiverSession.sendMessage(message);
            } catch (IOException | IllegalStateException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        UserData userData = (UserData) session.getAttributes().get("userFromJWT");
        sessionContext.removeSessionByUserId(userData.getId());
    }


    private Message messageParse(TextMessage message) {
        String jsonMsg = message.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();
        Message msg = null;
        try {
            msg = objectMapper.readValue(jsonMsg, Message.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return msg;
    }
}