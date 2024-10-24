package com.hcc.socket.server_test;

import com.hcc.socket.SocketApplication;
import com.hcc.socket.chat.config.WebSocketConfig;
import com.hcc.socket.chat.infra.RoomRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.*;
import java.util.stream.IntStream;

@SpringBootTest(classes = SocketApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketTest {

    @LocalServerPort
    int port;

    @Autowired
    WebSocketConfig webSocketConfig;

    @Autowired
    RoomRepository roomRepository;


    @BeforeEach
    void init() {

    }


    @Test
    @Transactional
    public void 웹소켓_테스트() throws Exception {
        // 준비
        String roomId = "1";
        Client client1 = new Client("test_1", port, webSocketConfig.PATH);
        Client client2 = new Client("test_2", port, webSocketConfig.PATH);
        List<String> users = Arrays.asList(client1.user.getId(), client2.user.getId());

        // 행동
        client1.sendMessage("message", "hi, i am c1", roomId);
        client1.sendMessage("message", "hi too. i am c2", roomId);
        Thread.sleep(1000);

        // 결과
        Assertions.assertTrue(client1.session.isOpen());
        Assertions.assertTrue(client2.session.isOpen());
        Assertions.assertEquals(client1.savedMsgs.size(), 2);
        Assertions.assertEquals(client2.savedMsgs.size(), 2);
        boolean b = IntStream.range(0, client1.savedMsgs.size())
                .allMatch(i -> Objects.equals(client1.savedMsgs.get(i).getPayload(), client2.savedMsgs.get(i).getPayload()));
        Assertions.assertTrue(b);
    }


}

