package com.example.feedservice.feed.service;

import com.example.feedservice.feed.dto.response.member.ResponseMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * 외부 API와 통신을 위한 클래스
 * */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedRestService {

    private final WebClient webClient;


    @Value("${domain}")
    private String domain;

    /**
     * 멤버 서비스와 통신
     * @param (memberIds) - List
     * */
    protected Mono<List<ResponseMemberDto>> communicateMemberService(List<String> memberIds, String token) {

        String domainPlusPort = domain + ":8081";

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(domainPlusPort + "/members/getProfile").queryParam("memberIds", memberIds);

        // URI 빌드
        URI uri = uriBuilder.build().encode().toUri();

        // URL + GET 매핑 조회 및 반환
        return webClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ResponseMemberDto>>() {
                });
    }
}
