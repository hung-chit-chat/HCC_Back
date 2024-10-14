package com.example.feedservice.feed.service;

import com.example.feedservice.comment.dto.CommentDto;
import com.example.feedservice.feed.dto.request.RequestFeedCursorDto;
import com.example.feedservice.feed.dto.request.RequestFeedUpdateDto;
import com.example.feedservice.feed.dto.response.ResponseFeedDto;
import com.example.feedservice.feed.dto.response.ResponseSuccessDto;
import com.example.feedservice.feed.dto.response.feed.FeedDto;
import com.example.feedservice.feed.dto.response.member.ResponseMemberInfoDto;
import com.example.feedservice.media.dto.MediaDto;
import com.example.feedservice.media.service.MediaService;
import com.example.feedservice.feed.dto.request.RequestFeedCreateDto;
import com.example.feedservice.feed.entity.FeedEntity;
import com.example.feedservice.feed.repository.FeedRepository;
import com.example.feedservice.common.util.FeedUtil;
import com.example.feedservice.reaction.dto.ReactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    private final MediaService mediaService;
    private final FeedUtil feedUtil;
    private final RedisTemplate<String, Object> redisTemplate;

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
     * @param (requestFeedCursorDto) - 커서 시간 지정 ( LocalDateTime )
     * @return FeedDto
     * */
    public ResponseFeedDto getFeedList(RequestFeedCursorDto requestFeedCursorDto) {

        LocalDateTime cursor = requestFeedCursorDto.getCursorDate();

        if(cursor == null){
            cursor = LocalDateTime.now();
        }

        try{
            if(isValidLocalDateTime(cursor)) {
                Object o = redisTemplate.opsForValue().get(cursor.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                // Redis 체크 및 확인
                if(true){

                } else {

                    // Feed Entity 조회 FetchJoin
                    List<FeedEntity> feedEntities = feedRepository.findFeedByCursor(cursor);

                    // Feed Entity 에서 memberId 추출
                    Set<String> memberIds = feedEntities.stream()
                            .map(FeedEntity::getMemberId)
                            .collect(Collectors.toSet());

                    // TODO :: member ID 로 rest 조회 , feedEntities 와 반환된 member 와 순서 맞춰서 DTO 반환
                    List<ResponseMemberInfoDto> memberFromMemberService = this.getMemberFromMemberService(memberIds);
                    Map<String, ResponseMemberInfoDto> memberMap = memberFromMemberService.stream().collect(Collectors.toMap(ResponseMemberInfoDto::getMemberId, ResponseMemberInfoDto -> ResponseMemberInfoDto));

                    List<FeedDto> collect = feedEntities.stream().map(feedEntity -> {
                        ResponseMemberInfoDto responseMemberInfoDto = memberMap.get(feedEntity.getMemberId());

                        return FeedDto.builder()
                                .feedId(feedEntity.getFeedId())
                                .contents(feedEntity.getContents())
                                .member(responseMemberInfoDto)
                                .publicScope(feedEntity.getPublicScope())
                                .commentDtos(feedEntity.getCommentList().stream().map(commentEntity -> new CommentDto(commentEntity.getCommentId(), commentEntity.getContents())).toList())
                                .mediaDtos(feedEntity.getMediaList().stream().map(mediaEntity -> new MediaDto(mediaEntity.getMediaId(), mediaEntity.getMediaPath())).toList())
                                .reactionDtos(feedEntity.getReactionList().stream().map(reactionEntity -> new ReactionDto(reactionEntity.getReactionId())).toList())
                                .build();
                    }).toList();

                    return ResponseFeedDto.builder()
                            .feedDto(collect)
                            .hssMore(true)
                            .build();
                }
            }
        } catch(DateTimeParseException e){
            throw new IllegalArgumentException("Invalid cursor date");
        }

        throw new RuntimeException("11");
    }

    /**
     * 입력받은 매개변수가 옳은 형식인지 비교
     * @param (cursor) - LocalDateTime
     * @return boolean
     * */
    protected boolean isValidLocalDateTime(LocalDateTime cursor) {
        try{
            // ISO_LOCAL_DATE_TIME = yyyy-MM-ddTHH:mm:ss 형식
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime.parse(cursor.toString(), formatter);
            return true;
        } catch(DateTimeParseException e){
            // 잘못된 형식일 시 예외 발생
            return false;
        }
    }

    /**
     * TODO :: 멤버 ID 로 REST 통신
     * */
    protected List<ResponseMemberInfoDto> getMemberFromMemberService(Set<String> memberIds){

        ResponseMemberInfoDto response = ResponseMemberInfoDto.builder().build();

        List<ResponseMemberInfoDto> responseMemberInfoDtos = new ArrayList<>();
        responseMemberInfoDtos.add(response);

        return responseMemberInfoDtos;

    }
}
