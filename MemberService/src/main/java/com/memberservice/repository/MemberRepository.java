package com.memberservice.repository;

import com.memberservice.exception.MemberNotFoundException;
import com.memberservice.model.entity.member.Member;
import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface MemberRepository extends Repository<Member, String> {

    Member save(Member member);

    Optional<Member> findByMemberId(String memberId);

    Optional<Member> findByEmail(String email);

    default Member findByMemberIdT(String memberId) {
        return findByMemberId(memberId).orElseThrow(() -> new MemberNotFoundException("토큰에 있는 사용자를 찾을 수 없음"));
    }

    default Member findByMemberIdP(String memberId) {
        return findByMemberId(memberId).orElseThrow(() -> new MemberNotFoundException("주어진 맴버아이디와 일치하는 사용자를 찾을 수 없음"));
    }
}
