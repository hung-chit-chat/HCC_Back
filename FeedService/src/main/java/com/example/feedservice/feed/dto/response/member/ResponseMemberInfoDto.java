package com.example.feedservice.feed.dto.response.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseMemberInfoDto {

    private String memberId;

    private String memberProfilePath;

    @Builder
    public ResponseMemberInfoDto(String memberId, String memberProfilePath) {
        this.memberId = memberId;
        this.memberProfilePath = memberProfilePath;
    }
}
