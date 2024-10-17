package com.example.feedservice.feed.dto.response.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseMemberDto {

    private String memberId;

    private String email;

    private String name;

    private String gender;

    private String phoneNumber;

    private String profileImgPath;

    @Builder
    public ResponseMemberDto(String memberId, String email, String name, String gender, String phoneNumber, String profileImgPath) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.profileImgPath = profileImgPath;
    }
}
