//package com.memberservice.service;
//
//import com.memberservice.config.AppConfig;
//import com.memberservice.config.FilePath;
//import com.memberservice.config.MockAppConfig;
//import com.memberservice.exception.PasswordNotMatchException;
//import com.memberservice.mock.FakeMemberRepository;
//import com.memberservice.model.entity.member.Gender;
//import com.memberservice.model.entity.member.Member;
//import com.memberservice.model.entity.member.Role;
//import com.memberservice.service.dto.request.SignUpMemberDto;
//import com.memberservice.service.dto.response.Profile;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.List;
//
//class MemberServiceTest {
//
//    MemberService memberService;
//    FakeMemberRepository memberRepository;
//    PasswordEncoder passwordEncoder;
//
//
//    @BeforeEach
//    void init() {
//        memberRepository = new FakeMemberRepository();
//        passwordEncoder = MockAppConfig.passwordEncoder();
//        this.memberService = new MemberService(
//                memberRepository,
//                passwordEncoder,
//                MockAppConfig.objectMapper(),
//                MockAppConfig.redisTemplate(),
//                null
//        );
//    }
//
//
//    @Test
//    @DisplayName("회원가입")
//    void signUp() {
//        // given
//        SignUpMemberDto dto = new SignUpMemberDto(
//                "test@test.com",
//                "asdf",
//                "김영감",
//                Gender.MALE,
//                "010-0000-0000",
//                Role.USER);
//
//        // when
//        String memberId = memberService.signUp(dto);
//
//        // then
//        Member member = memberRepository.findByMemberIdT(memberId);
//        Assertions.assertNotNull(member);
//        Assertions.assertEquals(member.getEmail(), "test@test.com");
//    }
//
//    @Test
//    @DisplayName("로그인")
//    void signIn() {
//        // todo
//    }
//
//    @Test
//    @DisplayName("휴대폰 번호 변경")
//    void changePhoneNumber() {
//        // given
//        SignUpMemberDto dto = new SignUpMemberDto(
//                "test@test.com",
//                "asdf",
//                "김영감",
//                Gender.MALE,
//                "010-0000-0000",
//                Role.USER);
//        String memberId = memberService.signUp(dto);
//
//        // when
//        memberService.changePhoneNumber(memberId, "010-1111-1111");
//        Member findMember = memberRepository.findByMemberIdT(memberId);
//
//        // then
//        Assertions.assertEquals(findMember.getPhoneNumber(), "010-1111-1111");
//        Assertions.assertNotEquals(findMember.getPhoneNumber(), "010-0000-0000");
//    }
//
//    @Test
//    @DisplayName("비밀번호 변경")
//    void changePassword() {
//        // given
//        SignUpMemberDto dto = new SignUpMemberDto(
//                "test@test.com",
//                "asdf",
//                "김영감",
//                Gender.MALE,
//                "010-0000-0000",
//                Role.USER);
//        String memberId = memberService.signUp(dto);
//
//        // when
//        memberService.changePassword(memberId, "asdf", "1234");
//        Member member = memberRepository.findByMemberIdT(memberId);
//
//        // then
//        boolean prevPwMatch = passwordEncoder.matches("asdf", member.getPassword());
//        Assertions.assertFalse(prevPwMatch);
//        boolean curPwMatch = passwordEncoder.matches("1234", member.getPassword());
//        Assertions.assertTrue(curPwMatch);
//    }
//
//
//    @Test
//    @DisplayName("비번 변경 시 기존비번이 틀리면 에러 남")
//    void changePassword2() {
//        // given
//        SignUpMemberDto dto = new SignUpMemberDto(
//                "test@test.com",
//                "asdf",
//                "김영감",
//                Gender.MALE,
//                "010-0000-0000",
//                Role.USER);
//        String memberId = memberService.signUp(dto);
//
//        // then
//        Assertions.assertThrows(PasswordNotMatchException.class, () -> {
//            memberService.changePassword(memberId, "틀린비번", "1234");
//        });
//    }
//
//
//    @Test
//    @DisplayName("프로필 가져오기 테스트")
//    void getProfile() {
//        // given
//        SignUpMemberDto dto = new SignUpMemberDto(
//                "test@test.com",
//                "asdf",
//                "김영감",
//                Gender.MALE,
//                "010-0000-0000",
//                Role.USER);
//        String memberId = memberService.signUp(dto);
//
//        // when
//        List<Profile> profiles = memberService.getProfile(List.of(memberId));
//        Profile profile = profiles.get(0);
//
//        //then
//        Assertions.assertNotNull(profile);
//        Assertions.assertEquals(profile.getMemberId(), memberId);
//        Assertions.assertEquals(profile.getEmail(), "test@test.com");
//        Assertions.assertEquals(profile.getName(), "김영감");
//        Assertions.assertEquals(profile.getGender(), Gender.MALE);
//        Assertions.assertEquals(profile.getPhoneNumber(), "010-0000-0000");
//    }
//
//    //todo: 프로필 저장 테스트 추가
////    public static void main(String[] args) {
////        MemberServiceTest test = new MemberServiceTest();
////        test.init();
////        test.getProfileImagePath();
////    }
//}