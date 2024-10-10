package com.memberservice.mock;


import com.memberservice.service.port.IdentifierFactory;

public class StubIdentifierFactory implements IdentifierFactory {

    @Override
    public String generate() {
        return "aaa-bbb-ccc";
    }
}
