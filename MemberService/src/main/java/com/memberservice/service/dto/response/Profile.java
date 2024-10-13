package com.memberservice.service.dto.response;

import com.memberservice.model.entity.member.Gender;
import com.memberservice.model.entity.member.Member;
import lombok.Getter;

@Getter
public class Profile {

    private final String memberId;
    private final String email;
    private final String name;
    private final Gender gender;
    private final String phoneNumber;
    private final String profileImgPath;

    public Profile(String memberId, String email, String name, Gender gender, String phoneNumber, String profileImgPath) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.profileImgPath = profileImgPath;
    }


    public static Profile from(Member member) {
        return new Profile(
                member.getMemberId()
                , member.getEmail()
                , member.getName()
                , member.getGender()
                , member.getPhoneNumber()
                , member.getProfileImgPath());
    }
}
