package com.memberservice.model.dto.response;

import com.memberservice.model.entity.member.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseMemberDto {

    private String memberId;
    private String email;
    private Role role;
    private String password;

    @Builder
    public ResponseMemberDto(String memberId, String email, Role role, String password) {
        this.memberId = memberId;
        this.email = email;
        this.role = role;
        this.password = password;
    }
}
