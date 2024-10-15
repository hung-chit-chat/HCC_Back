package com.example.feedservice.feed.dto.response.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseMemberProfileDto {

    private String memberId;

    private String memberProfilePath;

    @Builder
    public ResponseMemberProfileDto(String memberId, String memberProfilePath) {
        this.memberId = memberId;
        this.memberProfilePath = memberProfilePath;
    }
}
