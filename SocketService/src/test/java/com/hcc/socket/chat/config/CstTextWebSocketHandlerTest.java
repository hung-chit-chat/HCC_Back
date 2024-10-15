package com.hcc.socket.chat.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcc.socket.chat.SessionContext;
import com.hcc.socket.chat.infra.RoomRepository;
import com.hcc.socket.chat.infra.UserRepository;
import com.hcc.socket.chat.model.Message;
import com.hcc.socket.chat.model.Room;
import com.hcc.socket.chat.model.User;
import com.hcc.socket.chat.model.UserRoom;
import com.hcc.socket.chat.service.ChatService;
import com.hcc.socket.comm.IdentifierFactory;
import com.hcc.socket.comm.UUIDFactory;
import com.hcc.socket.mock.FakeRoomRepository;
import com.hcc.socket.mock.FakeUserRepository;
import com.hcc.socket.mock.StubIdentifierFactory;
import com.hcc.socket.security.jwt.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CstTextWebSocketHandlerTest {

    FakeUserRepository userRepository;
    FakeRoomRepository roomRepository;
    StubIdentifierFactory identifierFactory;
    ChatService chatService;
    SessionContext sessionContext;
    CstTextWebSocketHandler cstTextWebSocketHandler;

    @BeforeEach
    void init() {
        userRepository = new FakeUserRepository();
        roomRepository = new FakeRoomRepository();
        identifierFactory = new StubIdentifierFactory();
        chatService = new ChatService(identifierFactory, roomRepository);
        sessionContext = new SessionContext(roomRepository);
        this.cstTextWebSocketHandler = new CstTextWebSocketHandler(userRepository, sessionContext);
    }



    @Test
    void afterConnectionEstablished() {
        // given
        String userId = "test_id";
        UserData userData = new UserData(userId);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("userFromJWT", userData);
        WebSocketSession session = new StandardWebSocketSession(null, attributes, null, null);

        // when
        cstTextWebSocketHandler.afterConnectionEstablished(session);
        User user = userRepository.getEntities().get(0);
        WebSocketSession savedSession = sessionContext.getSessionByUserId("test_id");

        // then
        Assertions.assertEquals(user.getId(), "test_id");
        Assertions.assertEquals(session.getId(), savedSession.getId());
    }

    @Test
    void handleTextMessage() throws JsonProcessingException {
        // given
        String userId = "test_id";
        UserData userData = new UserData(userId);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("userFromJWT", userData);
        WebSocketSession session = new StandardWebSocketSession(null, attributes, null, null);

        cstTextWebSocketHandler.afterConnectionEstablished(session);

        User user = userRepository.getEntities().get(0);
        chatService.createRoom(user, null);
        WebSocketSession cachedSession = sessionContext.getCachedSessions().get("test_id");

        String stubRoomId = identifierFactory.generate();
        Message message = new Message(stubRoomId, null, "test message");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = objectMapper.writeValueAsString(message);
        TextMessage textMessage = new TextMessage(jsonMessage);

        // when
        cstTextWebSocketHandler.handleTextMessage(cachedSession, textMessage);
        Room cachedRoom = sessionContext.getCachedRooms().get(stubRoomId);

        // then
        Assertions.assertEquals(session.getId(), cachedSession.getId());
        Assertions.assertEquals(cachedRoom.getId(), "aaa-bbb-ccc");
    }
}