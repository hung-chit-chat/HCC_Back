package com.example.feedservice.feed.service;

import com.example.feedservice.feed.dto.redis.CursorDto;
import com.example.feedservice.feed.dto.request.RequestFeedCursorDto;
import com.example.feedservice.feed.dto.response.ResponseFeedDto;
import com.example.feedservice.feed.dto.response.feed.FeedListDto;
import com.example.feedservice.feed.dto.response.member.ResponseMemberDto;
import com.example.feedservice.feed.dto.response.member.ResponseMemberProfileDto;
import com.example.feedservice.feed.entity.FeedEntity;
import com.example.feedservice.media.dto.MediaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 비동기 처리를 위한 클래스
 * */

@Service
@Transactional(readOnly = true)
public class FeedMonoService {

    private final RedisTemplate<String, ResponseFeedDto> redisTemplate;

    private final FeedRestService feedRestService;

    private static final int BATCH_SIZE = 5;

    public FeedMonoService(@Qualifier("feedListRedisTemplate") RedisTemplate<String, ResponseFeedDto> redisTemplate, FeedRestService feedRestService) {
        this.redisTemplate = redisTemplate;
        this.feedRestService = feedRestService;
    }

    /**
     * member Service 에 비동기로 통신
     * */
    protected Mono<ResponseFeedDto> getMemberFromMemberService(String cursor, List<String> memberIds, List<FeedEntity> feedEntities, String token){

        return feedRestService.communicateMemberService(memberIds, token).map(memberFromMemberService -> {
            // Map<memberId, responseMemberDto> 데이터 -> 맵으로 stream
            Map<String, ResponseMemberDto> memberMap = memberFromMemberService.stream().collect(Collectors.toMap(ResponseMemberDto::getMemberId, ResponseMemberDto -> ResponseMemberDto));

            List<FeedListDto> collect = feedEntities.stream().map(feedEntity -> {
                // Feed - MemberId 매칭
                ResponseMemberDto responseMemberDto = memberMap.get(feedEntity.getMemberId());

                return FeedListDto.builder()
                        .feedId(feedEntity.getFeedId())
                        .contents(feedEntity.getContents())
                        .member(new ResponseMemberProfileDto(responseMemberDto.getMemberId(), responseMemberDto.getProfileImgPath()))
                        .publicScope(feedEntity.getPublicScope())
                        .createdDate(feedEntity.getCreatedDate())
                        .commentCount(feedEntity.getCommentList().size())           // 댓글 수만
                        .mediaDtos(feedEntity.getMediaList().stream().map(mediaEntity -> new MediaDto(mediaEntity.getMediaId(), mediaEntity.getMediaPath(), mediaEntity.getSequence())).toList())
                        .reactionCount(feedEntity.getReactionList().size())         // 좋아요 수만
                        .build();
            }).toList();

            // DB 에서 select 한 데이터 중 5개만 제외하고 나머지는 레디스에 저장, 제외한 데이터는 반환
            return this.saveRedisAndReturnRemainingFeed(cursor, collect);
        });
    }

    /**
     * 데이터(0~15) 중 5개를 제외한 나머지 데이터 레디스에 저장
     * 제외한 데이터는 반환
     * */
    protected ResponseFeedDto saveRedisAndReturnRemainingFeed(String cursor, List<FeedListDto> feedList){
        // 5개를 제외한 데이터 저장
        List<FeedListDto> feedSkipList = feedList.size() > 5 ? feedList.subList(5, feedList.size()) : Collections.emptyList();

        // feedList 가 5개보다 많으면 반환하고 남은 데이터중 첫번째 createDate 를 저장
        LocalDateTime returnNextCursorDate = !feedSkipList.isEmpty() ? this.getFirstCreatedDate(feedSkipList) : null;

        // 데이터가 남았는지 확인
        if(returnNextCursorDate != null){
            int remainingSize = feedSkipList.size();        // 반환하고 남은 데이터
            int loopCount = (int) Math.ceil((double) remainingSize / BATCH_SIZE);        // 나눠야 할 배치 수 계산 1~2    0이면 5개 이하

            for (int i = 0; i < loopCount; i++) {
                boolean hasMore = (i + 1 < loopCount);

                // 현재 배치에서 5개씩 데이터 가져옴
                List<FeedListDto> feedBatch = feedList.stream()
                        .skip((long) (i + 1) * BATCH_SIZE)
                        .limit(BATCH_SIZE)
                        .toList();

                LocalDateTime nextCursorDate = null;

                // 다음 배치의 첫 번째 항목을 nextCursorDate 로 설정
                if(hasMore){
                    List<FeedListDto> nextFeedBatch = feedSkipList.stream()
                            .skip((long) (i + 1) * BATCH_SIZE)  // 다음 배치로 넘어감
                            .limit(1)  // 첫 번째 항목만 가져옴
                            .toList();
                    if(!nextFeedBatch.isEmpty()){
                        nextCursorDate = this.getFirstCreatedDate(nextFeedBatch);
                    }
                }

                this.saveRedis(feedBatch, nextCursorDate, hasMore);
            }

            // 첫 데이터 5개를 반환
            return ResponseFeedDto.builder()
                    .cursorDate(cursor)
                    .feedListDto(feedList.stream().limit(5).toList())
                    .nextCursorDate(returnNextCursorDate.toString())
                    .hasMore(true)
                    .build();

        } else{
            // 데이터가 5개 이하인 경우 모두 반환
            return ResponseFeedDto.builder()
                    .cursorDate(cursor)
                    .feedListDto(feedList)
                    .nextCursorDate(null)
                    .hasMore(false)
                    .build();
        }
    }

    /**
     * 레디스에 저장
     * */
    protected void saveRedis(List<FeedListDto> feedBatch, LocalDateTime nextCursorDate, boolean hasMore){
        String redisKey = this.getFirstCreatedDate(feedBatch).toString();

        ResponseFeedDto batchResponse = ResponseFeedDto.builder()
                .feedListDto(feedBatch)
                .cursorDate(nextCursorDate != null ? nextCursorDate.toString() : this.getFirstCreatedDate(feedBatch).toString())
                .nextCursorDate(nextCursorDate != null ? nextCursorDate.toString() : null)
                .hasMore(hasMore)
                .build();

        redisTemplate.opsForValue().set(redisKey,
                batchResponse, 600, TimeUnit.SECONDS);
    }

    /**
     * 리스트 중 첫번째 createdDate 반환
     * */
    protected LocalDateTime getFirstCreatedDate(List<FeedListDto> feedBatch){
        return feedBatch.get(0).getCreatedDate();
    }
}
