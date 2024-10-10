package com.memberservice.mock;


import com.memberservice.model.entity.member.Member;
import com.memberservice.repository.MemberRepository;

import java.util.*;

public class FakeMemberRepository implements MemberRepository {

    final List<Member> members = new ArrayList<>();

    public List<Member> getList() {
        return members;
    }

    @Override
    public Member save(Member member) {
        if (member.getMemberId() == null || member.getMemberId().isBlank()) {
            Member newMember = Member.builder()
                    .memberId(member.getMemberId())
                    .email(member.getEmail())
                    .password(member.getPassword())
                    .name(member.getName())
                    .gender(member.getGender())
                    .phoneNumber(member.getPhoneNumber())
                    .role(member.getRole())
                    .build();
            members.add(newMember);
            return newMember;
        } else {
            members.removeIf(item -> Objects.equals(item.getMemberId(), member.getMemberId()));
            members.add(member);
            return member;
        }
    }


    @Override
    public Optional<Member> findByEmail(String email) {
        return members.stream().filter(member -> member.getEmail().equals(email)).findAny();
    }

    @Override
    public Optional<Member> findByMemberId(String memberId) {
        return members.stream().filter(member -> member.getMemberId().equals(memberId)).findAny();
    }
}
