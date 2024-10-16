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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
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

    private final RedisTemplate<CursorDto, ResponseFeedDto> redisTemplate;

    private final FeedRestService feedRestService;

    public FeedMonoService(@Qualifier("feedListRedisTemplate") RedisTemplate<CursorDto, ResponseFeedDto> redisTemplate, FeedRestService feedRestService) {
        this.redisTemplate = redisTemplate;
        this.feedRestService = feedRestService;
    }

    /**
     * member Service 에 비동기로 통신
     * */
    protected Mono<ResponseFeedDto> getMemberFromMemberService(RequestFeedCursorDto requestFeedCursorDto, List<String> memberIds, List<FeedEntity> feedEntities){

        return feedRestService.communicateMemberService(memberIds).map(memberFromMemberService -> {
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
            return this.saveRedisAndReturnRemainingFeed(requestFeedCursorDto.getCursorDate(), collect);
        });
    }

    /**
     * 데이터(0~15) 중 5개를 제외한 나머지 데이터 레디스에 저장
     * 제외한 데이터는 반환
     * */
    private ResponseFeedDto saveRedisAndReturnRemainingFeed(LocalDateTime cursorDate, List<FeedListDto> feedList){
        // 5개를 제외한 데이터 저장
        List<FeedListDto> feedSkipList = feedList.stream().skip(5).toList();

        // 데이터가 남았는지 확인
        if(!feedSkipList.isEmpty()){
            int batchSize = 5;
            int remainingSize = feedSkipList.size();        // 반환하고 남은 데이터
            int loopCount = (int) Math.ceil((double) remainingSize / batchSize);

            for (int i = 0; i < loopCount; i++) {
                boolean hasMore = true;

                // 현재 배치에서 5개씩 데이터 가져옴
                List<FeedListDto> feedBatch = feedSkipList.stream()
                        .skip((long) i * batchSize)
                        .limit(batchSize)
                        .toList();


                // 마지막 루프면 false
                if(i == loopCount - 1){
                    hasMore = false;
                }

                // 각 배치의 마지막 데이터를 새로운 커서로 설정
                LocalDateTime newCursorDate = feedBatch.get(feedBatch.size() - 1).getCreatedDate();

                ResponseFeedDto batchResponse = ResponseFeedDto.builder()
                        .feedListDto(feedBatch)
                        .hasMore(hasMore)
                        .build();

                redisTemplate.opsForValue().set(new CursorDto(newCursorDate),
                        batchResponse, 600, TimeUnit.SECONDS); // 각 배치를 저장할 때, 새로운 커서를 키로 사용하여 Redis 에 저장

            }

            // 첫 데이터 5개를 반환
            return ResponseFeedDto.builder()
                    .cursorDate(cursorDate)
                    .feedListDto(feedList.stream().limit(5).toList())
                    .hasMore(true)
                    .build();

        } else{

            // 데이터가 5개 이하인 경우 모두 반환
            return ResponseFeedDto.builder()
                    .cursorDate(cursorDate)
                    .feedListDto(feedList)
                    .hasMore(false)
                    .build();
        }
    }
}
