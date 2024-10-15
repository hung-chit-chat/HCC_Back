package com.hcc.socket.security.jwt.port;

import com.hcc.socket.security.jwt.UserData;

public interface DecodedJWT {

    UserData extractAuthentication(String token);
}
