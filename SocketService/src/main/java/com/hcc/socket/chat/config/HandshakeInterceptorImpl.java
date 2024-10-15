package com.hcc.socket.chat.config;

import com.hcc.socket.security.jwt.JWTHolder;
import com.hcc.socket.security.jwt.MockDecodedJWT;
import com.hcc.socket.security.jwt.UserData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandshakeInterceptorImpl implements HandshakeInterceptor {

    /**
     * @return true: 핸드셰이크 진행 | false: 중단
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String authorizationHeader = request.getHeaders().getFirst("Authorization");
        JWTHolder JWTHolder = new JWTHolder(authorizationHeader, new MockDecodedJWT());

        if (!JWTHolder.existToken()) {
            log.error("some by connection try, but no have token");
            return false;
        }

        UserData userData = JWTHolder.extractAuthentication();
        if (userData == null) {
            log.error("some by connection try, but no have user data");
            return false;
        }

        attributes.put("userFromJWT", userData);
        return true;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }


}
