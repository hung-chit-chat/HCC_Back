package com.hcc.socket.mock;


import com.hcc.socket.comm.IdentifierFactory;

public class StubIdentifierFactory implements IdentifierFactory {

    @Override
    public String generate() {
        return "aaa-bbb-ccc";
    }
}
