package com.example.feedservice.common.entity;

import com.auth0.jwt.JWT;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    /**
     * 토큰에 담겨있는 Claim 값 추출
     * @param token 토큰
     * @return 토큰에서 추출한 Claim
     * */
    public String extractClaim(String token, String claim) {
        return JWT.decode(token).getClaim(claim).asString();
    }

}
