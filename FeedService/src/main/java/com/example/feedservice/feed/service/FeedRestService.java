package com.example.feedservice.feed.service;

import com.example.feedservice.feed.dto.response.member.ResponseMemberDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 외부 API와 통신을 위한 클래스
 * */
@Service
@Transactional(readOnly = true)
public class FeedRestService {


    @Value("${domain}")
    private String domain;

    /**
     * 멤버 서비스와 통신
     * @param (memberIds) - List
     * */
    protected Mono<List<ResponseMemberDto>> communicateMemberService(List<String> memberIds) {
        // URL 빌드
        WebClient webClient = WebClient.builder()
                .baseUrl(domain + ":8081")
                .build();

        // URL + GET 매핑 조회 및 반환
        return webClient.get()
                .uri("/getProfile/" + memberIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ResponseMemberDto>>() {
                });
    }
}
