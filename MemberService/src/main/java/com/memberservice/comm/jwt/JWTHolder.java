package com.memberservice.comm.jwt;


import com.memberservice.comm.jwt.port.DecodedJWT;

public class JWTHolder {

    private final String authorizationHeader;
    private final DecodedJWT decodedJWT;

    public JWTHolder(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
        this.decodedJWT = new DecodedAuth0();
    }

    public JWTHolder(String authorizationHeader, DecodedJWT decodedJWT) {
        this.authorizationHeader = authorizationHeader;
        this.decodedJWT = decodedJWT;
    }


    public boolean existToken() {
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }


    public String getToken() {
        return authorizationHeader.substring(7);
    }


    public UserData extractAuthentication() {
        String token = getToken();
        return decodedJWT.extractAuthentication(token);
    }

}
