package com.hcc.socket.comm;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDFactory implements IdentifierFactory {


    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}