package com.memberservice.comm.jwt;

import com.memberservice.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExtractAuthentication {

    public UserData execute(HttpServletRequest request) {
        String authorizationHeader = null;
        try {
            authorizationHeader = request.getHeader("Authorization");
        } catch (Exception e) {
            log.info("this");
        }
        String remoteAddr = request.getRemoteAddr();
        JWTHolder JWTHolder = new JWTHolder(authorizationHeader, new MockDecodedJWT()); // todo: test

        if (!JWTHolder.existToken()) {
            throw new AuthenticationException("엑세스 토큰 없음");
        } else {
            UserData authentication = JWTHolder.extractAuthentication();
            if (authentication == null) {
                throw new AuthenticationException("엑세스 토큰 내용이 유효하지 않음");
            }
            return authentication;
        }
    }

}
