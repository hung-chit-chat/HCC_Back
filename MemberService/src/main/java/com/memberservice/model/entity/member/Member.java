package com.memberservice.model.entity.member;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Member extends BaseEntity {


    @Id
    private String memberId; // prePersist

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    private Gender gender;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

//    d04e75dd-228a-4802-915d-ff432cf00554

    private String profileImgPath; // prePersist

    @PrePersist
    public void prePersist() {
        this.memberId = UUID.randomUUID().toString();
    }

    public void changePhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void changePassword(String newPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(newPassword);
    }

    public void changeProfileImgPath(String profileImgPath) {
        this.profileImgPath = profileImgPath;
    }
}
