package com.memberservice.service.dto.jackson;

import com.fasterxml.jackson.annotation.JsonView;
import com.memberservice.model.entity.member.Gender;
import com.memberservice.model.entity.member.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberView {

    @JsonView({Views.MemberIdAndEmailAndRole.class, Views.MemberIdAndProfileImgPath.class})
    private String memberId;

    @JsonView(Views.MemberIdAndEmailAndRole.class)
    private String email;

    private String password;

    private String name;

    private Gender gender;

    private String phoneNumber;

    @JsonView(Views.MemberIdAndEmailAndRole.class)
    private Role role;

    @JsonView(Views.MemberIdAndProfileImgPath.class)
    private String profileImgPath;
}
