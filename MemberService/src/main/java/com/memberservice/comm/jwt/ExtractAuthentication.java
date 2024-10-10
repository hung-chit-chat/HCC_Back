package com.memberservice.comm.jwt;

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
            log.error("some client {} request. but no have token", remoteAddr);
            throw new NullPointerException();
        } else {
            UserData authentication = JWTHolder.extractAuthentication();
            if (authentication == null) {
                log.error("some client {} request. but token is not collect", remoteAddr);
                throw new NullPointerException();
            }
            return authentication;
        }
    }

}
