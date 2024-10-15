package com.memberservice.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    Exception(0, "예외")
    , AuthenticationException(101, "인증 실패")
    , MemberNotFoundException(102, "맴버 조회 실패");

    private final int code;

    ErrorCode(int code ,String des) {
        this.code = code;
    }
}
