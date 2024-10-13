package com.memberservice.controller;

import com.memberservice.comm.jwt.ExtractAuthentication;
import com.memberservice.comm.jwt.UserData;
import com.memberservice.service.dto.request.Phone;
import com.memberservice.service.dto.request.ReqMember;
import com.memberservice.service.dto.request.RequestLoginDto;
import com.memberservice.service.dto.request.SignUpMemberDto;
import com.memberservice.service.dto.response.Profile;
import com.memberservice.service.dto.response.ResponseTokenDto;
import com.memberservice.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Slf4j
public class MemberRestController {

    private final MemberService memberService;
    private final ExtractAuthentication extractAuthentication;

    @GetMapping("/test")
    public String test() {
        return "test Online1234";
    }

    /**
     * 회원가입
     */
    @PostMapping("/auth/signUp")
    public ResponseEntity<Map<String, String>> sighUp(@RequestBody @Valid SignUpMemberDto dto) {

        try {
            memberService.signUp(dto);
            Map<String, String> result = new HashMap<>();
            result.put("result", "success");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 중복된 이메일
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }


    /**
     * 로그인
     */
    @PostMapping("/auth/signIn")
    public ResponseEntity<ResponseTokenDto> signIn (@RequestBody RequestLoginDto requestLoginDto) {

        ResponseTokenDto responseTokenDto = memberService.signIn(requestLoginDto);
        return ResponseEntity.ok(responseTokenDto);
    }




    @GetMapping("/getProfile")
    public ResponseEntity<List<Profile>> getProfile(HttpServletRequest request, @RequestParam(required = false) List<String> memberIds) {

        if(memberIds == null) {
            memberIds = new ArrayList<>();
        }

        if(memberIds.isEmpty()) {
            UserData userData = extractAuthentication.execute(request);
            memberIds.add(userData.getMemberId());
        }

        List<Profile> profiles = memberService.getProfile(memberIds);
        return ResponseEntity.ok(profiles);
    }


    @PostMapping("/changePassword")
    public void changePassword(HttpServletRequest request, @RequestBody ReqMember reqMember) {
        UserData userData = extractAuthentication.execute(request);
        memberService.changePassword(userData.getMemberId(), reqMember.getPassword(), reqMember.getNewPassword());
    }

    @PutMapping("/changePhoneNumber")
    public void changePhoneNumber(HttpServletRequest request,@Valid @RequestBody Phone phone) {
        UserData userData = extractAuthentication.execute(request);
        memberService.changePhoneNumber(userData.getMemberId(), phone.getNumber());
    }

    @PutMapping("/changeProfileImage")
    public void changeProfileImage(HttpServletRequest request, MultipartFile image) {
        UserData userData = extractAuthentication.execute(request);
        memberService.changeProfileImage(userData.getMemberId(), image);
    }
}
