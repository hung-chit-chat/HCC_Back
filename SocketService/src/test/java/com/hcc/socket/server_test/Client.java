package com.hcc.socket.server_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcc.socket.chat.model.Message;
import com.hcc.socket.chat.model.User;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Client {

    User user;
    List<TextMessage> savedMsgs = new ArrayList<>();
    WebSocketSession session;

    public Client(String userId, int port, String path) throws ExecutionException, InterruptedException, TimeoutException {
        this.user = User.builder().id(userId).build();
        URI webSocketUri = URI.create("ws://localhost:" + port + path);

        TextWebSocketHandler clientWebSocketHandler = new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            }

            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                savedMsgs.add(message);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            }
        };

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer id=" + userId);

        // 클라이언트 생성. !! 클라이언트에 들어가는 핸들러는 클라이언트가 메세지를 받았을 때 처리하는 방식을 정의하는 것이지,
        // config에 설정한 핸들러와는 전혀 별개이다.
        StandardWebSocketClient client = new StandardWebSocketClient();
        CompletableFuture<WebSocketSession> future = client.execute(clientWebSocketHandler, headers, webSocketUri);
        session = future.get(5, TimeUnit.SECONDS);
    }

    public void sendMessage(String type, String text, String roomId) throws IOException {
        Message msg = Message.builder()
                .roomId(roomId)
                .type(type)
                .text(text)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMsg = objectMapper.writeValueAsString(msg);
        TextMessage textMessage = new TextMessage(jsonMsg);
        session.sendMessage(textMessage);
    }
}