package com.memberservice.model.entity.member;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Member extends BaseEntity {


    @Id
    private String memberId;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    private Gender gender;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;



    public void changePhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void changePassword(String newPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(newPassword);
    }
}
