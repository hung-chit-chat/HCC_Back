package com.memberservice.comm.jwt;

import com.auth0.jwt.JWT;
import com.memberservice.comm.jwt.port.DecodedJWT;


public class DecodedAuth0 implements DecodedJWT {

    public UserData extractAuthentication(String token) {

//            String email = extractClaim(token, "email");
//            String memberId = extractClaim(token, "member_id");
//
//
//            if (email == null) {
//                return null;
//            }
//
//            String userRole = extractClaim(token, "role");
//
//            UserDetails userDetails = User.withUsername(email)
//                    .password("")
//                    .authorities(userRole)
//                    .build();
//
//            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return null;
    }

    private String extractClaim(String token, String claim) {
        return JWT.decode(token).getClaim(claim).asString();
    }
}
