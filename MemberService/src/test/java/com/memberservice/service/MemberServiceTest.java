package com.memberservice.service;

import com.memberservice.config.MockAppConfig;
import com.memberservice.exception.MemberNotFoundException;
import com.memberservice.exception.PasswordNotMatchException;
import com.memberservice.mock.FakeMemberRepository;
import com.memberservice.mock.StubIdentifierFactory;
import com.memberservice.model.entity.member.Gender;
import com.memberservice.model.entity.member.Member;
import com.memberservice.model.entity.member.Role;
import com.memberservice.repository.MemberRepository;
import com.memberservice.service.dto.request.SignUpMemberDto;
import com.memberservice.service.port.IdentifierFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

class MemberServiceTest {

    MemberService memberService;
    FakeMemberRepository memberRepository;
    PasswordEncoder passwordEncoder;
    final IdentifierFactory stubIdentifierFactory = new StubIdentifierFactory();

    @BeforeEach
    void init() {
        memberRepository = new FakeMemberRepository();
        passwordEncoder = MockAppConfig.passwordEncoder();
        this.memberService = new MemberService(
                memberRepository,
                passwordEncoder,
                MockAppConfig.objectMapper(),
                stubIdentifierFactory,
                MockAppConfig.redisTemplate()
        );
    }


    @Test
    @DisplayName("회원가입")
    void signUp() {
        // given
        SignUpMemberDto dto = new SignUpMemberDto(
                "test@test.com",
                "asdf",
                "김영감",
                Gender.MALE,
                "010-0000-0000",
                Role.USER);

        // when
        memberService.signUp(dto);

        // then
        Member member = memberRepository.cFindByMemberId(stubIdentifierFactory.generate());
        Assertions.assertNotNull(member);
        Assertions.assertEquals(member.getEmail(), "test@test.com");
    }

    @Test
    @DisplayName("로그인")
    void signIn() {
        // todo
    }

    @Test
    @DisplayName("휴대폰 번호 변경")
    void changePhoneNumber() {
        // given
        SignUpMemberDto dto = new SignUpMemberDto(
                "test@test.com",
                "asdf",
                "김영감",
                Gender.MALE,
                "010-0000-0000",
                Role.USER);
        memberService.signUp(dto);
        String memberId = stubIdentifierFactory.generate();

        // when
        memberService.changePhoneNumber(memberId, "010-1111-1111");
        Member member = memberRepository.cFindByMemberId(memberId);

        // then
        Assertions.assertEquals(member.getPhoneNumber(), "010-1111-1111");
        Assertions.assertNotEquals(member.getPhoneNumber(), "010-0000-0000");
    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePassword() {
        // given
        SignUpMemberDto dto = new SignUpMemberDto(
                "test@test.com",
                "asdf",
                "김영감",
                Gender.MALE,
                "010-0000-0000",
                Role.USER);
        memberService.signUp(dto);
        String memberId = stubIdentifierFactory.generate();

        // when
        memberService.changePassword(memberId, "asdf", "1234");
        Member member = memberRepository.cFindByMemberId(memberId);

        // then
        boolean prevPwMatch = passwordEncoder.matches("asdf", member.getPassword());
        Assertions.assertFalse(prevPwMatch);
        boolean curPwMatch = passwordEncoder.matches("1234", member.getPassword());
        Assertions.assertTrue(curPwMatch);
    }


    @Test
    @DisplayName("비번 변경 시 기존비번이 틀리면 에러 남")
    void changePassword2() {
        // given
        SignUpMemberDto dto = new SignUpMemberDto(
                "test@test.com",
                "asdf",
                "김영감",
                Gender.MALE,
                "010-0000-0000",
                Role.USER);
        memberService.signUp(dto);
        String memberId = stubIdentifierFactory.generate();

        // then
        Assertions.assertThrows(PasswordNotMatchException.class, () -> {
            memberService.changePassword(memberId, "틀린비번", "1234");
        });
    }
}