package com.memberservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memberservice.model.Converter;
import com.memberservice.model.dto.jackson.MemberView;
import com.memberservice.model.dto.jackson.Views;
import com.memberservice.model.dto.request.RequestLoginDto;
import com.memberservice.model.dto.request.SignUpMemberDto;
import com.memberservice.model.dto.response.ResponseMemberDto;
import com.memberservice.model.dto.response.ResponseTokenDto;
import com.memberservice.model.entity.member.Member;
import com.memberservice.repository.MemberRepository;
import com.memberservice.service.port.IdentifierFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final IdentifierFactory identifierFactory;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${domain.dev.jwt}")
    private String JWT_DOMAIN;


    // 회원가입
    @Transactional
    public Member signUp(SignUpMemberDto signUpMemberDto) {

        // 비밀번호 encode
        signUpMemberDto.EncodePassword(passwordEncoder.encode(signUpMemberDto.getPassword()));
        Member member = Converter.RequestToEntity(signUpMemberDto, identifierFactory.generate());

        return memberRepository.save(member);
    }

    // 로그인
    public ResponseTokenDto signIn(RequestLoginDto requestLoginDto) {

        // 여러번 로그인을 가정하고 Cache 사용 -> 1시간 이내 재 로그인시 성능 상승, 사용자 경험 개선
        // TODO :: (PC, Mobile 기기 ID 추출 가능하면 jwt 토큰에도 적용 할 예정)
        ResponseMemberDto responseMemberDto = (ResponseMemberDto) redisTemplate.opsForValue().get(requestLoginDto.getEmail());
        String jsonBody = null;
        MemberView memberView = null;

        // 레디스에 Member DTO 가 없으면 조회 후
        if (responseMemberDto == null) {
            Member member = memberRepository.findByEmail(requestLoginDto.getEmail()).orElseThrow();

            if (passwordEncoder.matches(requestLoginDto.getPassword(), member.getPassword())) {
                memberView = Converter.MemberToView(member);
            }

            ResponseMemberDto memberDto = ResponseMemberDto.builder()
                    .email(member.getEmail())
                    .password(member.getPassword())
                    .role(member.getRole())
                    .memberId(member.getMemberId())
                    .build();

            // 레디스에 1시간동안 저장
            redisTemplate.opsForValue().set(requestLoginDto.getEmail(), memberDto, 3600, TimeUnit.SECONDS);

        } else {
            if (passwordEncoder.matches(requestLoginDto.getPassword(), responseMemberDto.getPassword())) {
                memberView = Converter.ResponseMemberDtoToView(responseMemberDto);
            }
        }

        try {
            jsonBody = objectMapper.writerWithView(Views.MemberIdAndEmailAndRole.class).writeValueAsString(memberView);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if(jsonBody == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String uri = JWT_DOMAIN + ":8089/jwt/login"; // jwt service


        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("Content-Type", "application/json") // 요청 헤더 설정
                    .POST(BodyPublishers.ofString(jsonBody)) // 본문 추가
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();

            return objectMapper.readValue(body, ResponseTokenDto.class);
        } catch (Exception e) {
            // TODO :: 예외 찾아서 추가 작업 해야함
            throw new RuntimeException(e);
        }
    }

    // 프로필 사진 저장

    // 프로필 사진 변경

    // 비밀번호 변경

    // 휴대폰 번호 변경
    public void changePhoneNumber() {

    }

}
