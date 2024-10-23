package com.example.feedservice.feed.controller;

import com.example.feedservice.feed.dto.request.RequestFeedCreateDto;
import com.example.feedservice.feed.dto.request.RequestFeedCursorDto;
import com.example.feedservice.feed.dto.request.RequestFeedUpdateDto;
import com.example.feedservice.feed.dto.response.ResponseFeedDto;
import com.example.feedservice.feed.dto.response.ResponseSuccessDto;
import com.example.feedservice.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @PostMapping("/feeds")
    public ResponseEntity<ResponseSuccessDto> createFeed(@RequestBody RequestFeedCreateDto requestFeedCreateDto, @RequestHeader("Authorization") String bearerToken) {
        try {
            String token = null;
            if("Bearer ".equals(bearerToken.substring(0, 7))){
                token = bearerToken.substring(7);
            } else{
                throw new IllegalArgumentException("Invalid Bearer Token");
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(feedService.createFeed(requestFeedCreateDto, token));
        } catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseSuccessDto.builder().result("error").build());
        }
    }


    @PutMapping("{feedId}")
    public ResponseEntity<ResponseSuccessDto> updateFeed(@PathVariable String feedId, @RequestBody RequestFeedUpdateDto requestFeedUpdateDto, @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(feedService.updateFeed(feedId, requestFeedUpdateDto, token));
        } catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseSuccessDto.builder().result("error").build());
        }
    }

    /**
     * 전체 목록 조회, 커서 기반 전략
     * @param cursor - LocalDateTime -> String 으로 변환 후 넘겨받기
     * */
    @GetMapping
    public ResponseEntity<Mono<ResponseFeedDto>> getFeedList(@RequestParam(required = false) String cursor, @RequestHeader("Authorization") String bearerToken) {

        try{
            String token = null;
            if("Bearer ".equals(bearerToken.substring(0, 7))){
                token = bearerToken.substring(7);
            } else{
                throw new IllegalArgumentException("Invalid Bearer Token");
            }
            return ResponseEntity.status(HttpStatus.OK).body(feedService.getFeedList(cursor, token));
        }catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
