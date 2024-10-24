package com.memberservice.comm.jwt.port;


import com.memberservice.comm.jwt.UserData;

public interface DecodedJWT {

    UserData extractAuthentication(String token);
}
