package com.example.feedservice.feed.service;

import com.example.feedservice.feed.dto.response.member.ResponseMemberDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FeedRestServiceTest {

    @Autowired FeedRestService feedRestService;

    @DisplayName("외부 API 멤버 테스트")
    @Test
    public void testGetMemberFromMemberService(){

        List<String> memberIds = new ArrayList<>();

        memberIds.add("c90f6d83-3a00-45db-8ca1-c1a2d04795d2");

        Mono<List<ResponseMemberDto>> listMono = feedRestService.communicateMemberService(memberIds, "1111");

        List<ResponseMemberDto> block = listMono.block();

        assertNotNull(block);

        for (ResponseMemberDto responseMemberDto : block) {
            System.out.println("responseMemberDto.getMemberId() = " + responseMemberDto.getMemberId());
            System.out.println("responseMemberDto.getProfileImgPath() = " + responseMemberDto.getProfileImgPath());
            System.out.println("responseMemberDto.getEmail() = " + responseMemberDto.getEmail());
            System.out.println("responseMemberDto.getName() = " + responseMemberDto.getName());
        }

        /**
         * 테스트 완료 2024-10-16
         * */

    }
}