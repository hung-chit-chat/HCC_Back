package com.example.feedservice.feed.service;

import com.example.feedservice.feed.dto.request.RequestFeedCursorDto;
import com.example.feedservice.feed.dto.request.RequestFeedUpdateDto;
import com.example.feedservice.feed.dto.response.ResponseFeedDto;
import com.example.feedservice.feed.dto.response.ResponseSuccessDto;
import com.example.feedservice.media.service.MediaService;
import com.example.feedservice.feed.dto.request.RequestFeedCreateDto;
import com.example.feedservice.feed.entity.FeedEntity;
import com.example.feedservice.feed.repository.FeedRepository;
import com.example.feedservice.common.util.FeedUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    private final MediaService mediaService;
    private final FeedUtil feedUtil;
    private final RedisTemplate<String, ResponseFeedDto> redisTemplate;
    private final FeedMonoService feedMonoService;

    public FeedService(FeedRepository feedRepository, MediaService mediaService, FeedUtil feedUtil,@Qualifier("feedListRedisTemplate") RedisTemplate<String, ResponseFeedDto> redisTemplate, FeedMonoService feedMonoService) {
        this.feedRepository = feedRepository;
        this.mediaService = mediaService;
        this.feedUtil = feedUtil;
        this.redisTemplate = redisTemplate;
        this.feedMonoService = feedMonoService;
    }

    /**
     * 게시글 수정
     * @param (requestFeedCreateDto) - 피드 아이디, 업데이트 정보
     * @return "success"
     * */
    @Transactional
    public ResponseSuccessDto createFeed(RequestFeedCreateDto requestFeedCreateDto){

        String feedId = feedUtil.getUUID();

        // feed Entity 생성
        FeedEntity feed = FeedEntity.builder()
                .feedId(feedId)
                .memberId(requestFeedCreateDto.getMemberId())
                .contents(requestFeedCreateDto.getContents())
                .publicScope(requestFeedCreateDto.getPublicScope())
                .build();

        // 프론트에서 넘어온 Media 객체가 없으면 스킵
        if(!requestFeedCreateDto.getMedia().isEmpty()) {
            try {
                // File 로컬에 저장 및 연관관계 매핑, 영속성 컨텍스트에 들어간 Post Entity 저장
                mediaService.uploadMediaAtStore(feed, requestFeedCreateDto.getMedia());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file : " + e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        } else{
            feedRepository.save(feed);
        }

        return ResponseSuccessDto.builder()
                .result("success")
                .build();

    }

    /**
     * 게시글 수정
     * @param (feedId, requestFeedUpdateDto) - 피드 아이디, 업데이트 정보
     * @return "success"
     * */
    @Transactional
    public ResponseSuccessDto updateFeed(String feedId, RequestFeedUpdateDto requestFeedUpdateDto){

        FeedEntity feedEntity = feedRepository.findById(feedId).orElseThrow(() -> new IllegalArgumentException("Feed Not Found"));
        feedEntity.changeContents(requestFeedUpdateDto.getContents());      // 내용 변경

        // 프론트에서 넘어온 Media 객체가 없으면 스킵
        if(!requestFeedUpdateDto.getMedia().isEmpty()){
            try {
                mediaService.updateMediaAtStore(feedEntity, requestFeedUpdateDto.getMedia());
            } catch(Exception e){
                throw new RuntimeException("Failed to update media : " + e);
            }
        } else{
            feedRepository.save(feedEntity);
        }

        return ResponseSuccessDto.builder()
                .result("success")
                .build();
    }

    /**
     * 회원 목록 조회 ( 커서 기반 전략 )
     * @param (cursor) - 커서 시간 지정 ( LocalDateTime )
     * @return FeedDto
     * */
    public Mono<ResponseFeedDto> getFeedList(String cursor) {

        if(cursor == null || !isValidLocalDateTime(cursor)){
            cursor = LocalDateTime.now().toString();
        }

        try{
            // 커서(조회 시간)가 유효한지 체크 
                ResponseFeedDto responseFeedDto = redisTemplate.opsForValue().get(cursor);
                // Redis 체크 및 확인
                if(responseFeedDto != null){
                    return Mono.just(responseFeedDto);
                } else {

                    LocalDateTime cursorDate = LocalDateTime.parse(cursor);

                    List<FeedEntity> feedEntities = feedRepository.findFeedByCursor(cursorDate);

                    // Feed Entity 에서 memberId 추출
                    Set<String> memberIds = feedEntities.stream()
                            .map(FeedEntity::getFeedId)
                            .collect(Collectors.toSet());

                    // Set -> List
                    List<String> memberIdList = List.copyOf(memberIds);

                    return feedMonoService.getMemberFromMemberService(cursor, memberIdList, feedEntities);
                }
        } catch(DateTimeParseException e){
            throw new IllegalArgumentException("Invalid cursor date");
        }

    }

    /**
     * 입력받은 매개변수가 옳은 형식인지 비교
     * @param (cursor) - LocalDateTime
     * @return boolean
     * */
    protected boolean isValidLocalDateTime(String cursor) {
        try{
            // ISO_LOCAL_DATE_TIME = yyyy-MM-ddTHH:mm:ss 형식
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime.parse(cursor, formatter);
            return true;
        } catch(DateTimeParseException e){
            // 잘못된 형식일 시 예외 발생
            return false;
        }
    }



}
