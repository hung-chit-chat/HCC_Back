package com.memberservice.comm.jwt;

import com.memberservice.comm.jwt.port.DecodedJWT;
import org.springframework.stereotype.Component;

@Component
public class MockDecodedJWT implements DecodedJWT {

    public UserData extractAuthentication(String token) {

        String id = extractClaim(token, "id");
        if (id == null) {
            return null;
        }


        return new UserData(id);
    }

    private String extractClaim(String input, String key) {
        String[] pairs = input.split(","); // 콤마로 분리

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].trim().equals(key.trim())) {
                return keyValue[1].trim(); // 해당 키의 값 반환
            }
        }
        return null; // 키가 존재하지 않으면 null 반환
    }
}
