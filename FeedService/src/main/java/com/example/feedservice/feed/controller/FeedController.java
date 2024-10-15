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

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @PostMapping("/feeds")
    public ResponseEntity<ResponseSuccessDto> createFeed(@RequestBody RequestFeedCreateDto requestFeedCreateDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(feedService.createFeed(requestFeedCreateDto));
        } catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseSuccessDto.builder().result("error").build());
        }
    }


    @PutMapping("/{feedId}")
    public ResponseEntity<ResponseSuccessDto> updateFeed(@PathVariable String feedId, @RequestBody RequestFeedUpdateDto requestFeedUpdateDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(feedService.updateFeed(feedId, requestFeedUpdateDto));
        } catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseSuccessDto.builder().result("error").build());
        }
    }
    

    @GetMapping("/feeds")
    public ResponseEntity<Mono<ResponseFeedDto>> getFeedList(@RequestBody RequestFeedCursorDto requestFeedCursorDto) {

        try{
            return ResponseEntity.status(HttpStatus.OK).body(feedService.getFeedList(requestFeedCursorDto));
        }catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
