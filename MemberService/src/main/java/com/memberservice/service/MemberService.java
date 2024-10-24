package com.memberservice.service;

import java.nio.file.Files;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memberservice.config.FilePath;
import com.memberservice.exception.PasswordNotMatchException;
import com.memberservice.service.dto.Converter;
import com.memberservice.service.dto.jackson.MemberView;
import com.memberservice.service.dto.jackson.Views;
import com.memberservice.service.dto.request.RequestLoginDto;
import com.memberservice.service.dto.request.SignUpMemberDto;
import com.memberservice.service.dto.response.Profile;
import com.memberservice.service.dto.response.ResponseMemberDto;
import com.memberservice.service.dto.response.ResponseTokenDto;
import com.memberservice.model.entity.member.Member;
import com.memberservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final FilePath filePath;

    @Value("${domain.local}")
    private String JWT_DOMAIN;



    // 회원가입
    @Transactional
    public String signUp(SignUpMemberDto signUpMemberDto) {
        // 비밀번호 encode
        signUpMemberDto.EncodePassword(passwordEncoder.encode(signUpMemberDto.getPassword()));
        Member member = Converter.RequestToEntity(signUpMemberDto);
        Member saved = memberRepository.save(member);
        return saved.getMemberId();
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

    // 프로필 가져오기
    public List<Profile> getProfile(List<String> memberIds) {
        List<Profile> profiles = new ArrayList<>();
        memberIds.forEach(memberId -> {
            Member member = memberRepository.findByMemberIdP(memberId);
            profiles.add(Profile.from(member));
        });
        return profiles;
    }


    // 프로필 사진 변경
    @Transactional
    public void changeProfileImage(String memberId, MultipartFile image) {
        Member member = memberRepository.findByMemberIdT(memberId);
        String contentType = image.getContentType();
        String[] split = contentType.split("/");
        if (!split[0].equals("image")) {
            throw new RuntimeException("이미지가 아닙니다");
        }

        String normal = filePath.getNormal();
        String rPath = "/members/profileImg/" + member.getMemberId() + "." + split[1];
        Path aPath = Paths.get(normal + rPath);

        if (!Files.exists(aPath.getParent())) {
            try {
                Files.createDirectories(aPath.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            image.transferTo(aPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(rPath);
        System.out.println(aPath);
        member.changeProfileImgPath(rPath);
        memberRepository.save(member);
    }

    // 휴대폰 번호 변경
    @Transactional
    public void changePhoneNumber(String memberId, String newPhoneNumber) {
        Member member = memberRepository.findByMemberIdT(memberId);
        member.changePhoneNumber(newPhoneNumber);
        memberRepository.save(member);
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(String memberId, String password, String newPassword) {
        Member member = memberRepository.findByMemberIdT(memberId);
        boolean isMatch = passwordEncoder.matches(password, member.getPassword());
        if (!isMatch) {
            throw new PasswordNotMatchException("기존 비밀번호가 새 비밀번호와 일치하지 않음");
        }
        member.changePassword(newPassword, passwordEncoder);
        memberRepository.save(member);
    }
}
