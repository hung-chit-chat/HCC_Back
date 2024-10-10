package com.memberservice.model;

import com.memberservice.service.port.IdentifierFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDFactory implements IdentifierFactory {


    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
