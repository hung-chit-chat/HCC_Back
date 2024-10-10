package com.memberservice.model.entity.relationship;

import com.memberservice.model.entity.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;


/**
 * 맴버가 다른 맴버에 관련한 정보를 저장하는 테이블
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Relationship {


    @Id
    String id;

    @ManyToOne
    private Member actor; // 관계의 주체

    @ManyToOne
    private Member receiver; // 대상




}
