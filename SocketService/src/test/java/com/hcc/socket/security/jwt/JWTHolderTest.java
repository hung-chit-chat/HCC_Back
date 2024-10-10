package com.hcc.socket.security.jwt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JWTHolderTest {


    @Test
    @DisplayName("원하는 형태의 토큰이 있는가")
    void existToken() {
        // given
        String authorizationHeader = "Bearer id=testHolder";
        JWTHolder jwtHolder = new JWTHolder(authorizationHeader, new MockDecodedJWT());

        // when
        boolean existToken = jwtHolder.existToken();

        // then
        Assertions.assertTrue(existToken);
    }

    @Test
    @DisplayName("토큰이 null일때 false를 반환하는가")
    void existToken2() {
        // given
        String authorizationHeader = null;
        JWTHolder jwtHolder = new JWTHolder(authorizationHeader, new MockDecodedJWT());

        // when
        boolean existToken = jwtHolder.existToken();

        // then
        Assertions.assertFalse(existToken);
    }

    @Test
    @DisplayName("토큰이 Bearere 로 시작하는가")
    void existToken3() {
        // given
        String authorizationHeader = "NoBearer id=testHolder";
        JWTHolder jwtHolder = new JWTHolder(authorizationHeader, new MockDecodedJWT());

        // when
        boolean existToken = jwtHolder.existToken();

        // then
        Assertions.assertFalse(existToken);
    }

    @Test
    @DisplayName("토큰의 앞글자 7개는 필요없는가")
    void getToken() {
        // given
        String authorizationHeader = "Bearer token body";
        JWTHolder jwtHolder = new JWTHolder(authorizationHeader, new MockDecodedJWT());

        // when
        String token = jwtHolder.getToken();

        // then
        Assertions.assertEquals(token, "token body");
    }

    @Test
    @DisplayName("토큰에서 인증객체로 변환")
    void extractAuthentication() {
        // given
        String authorizationHeader = "Bearer id=test";
        JWTHolder jwtHolder = new JWTHolder(authorizationHeader, new MockDecodedJWT());

        // when
        UserData authentication = jwtHolder.extractAuthentication();
        String id = authentication.getId();

        // then
        Assertions.assertEquals(id, "test");
    }
}