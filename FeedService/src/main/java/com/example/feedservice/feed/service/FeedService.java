package com.example.feedservice.feed.service;

import com.example.feedservice.feed.dto.redis.CursorDto;
import com.example.feedservice.feed.dto.request.RequestFeedCursorDto;
import com.example.feedservice.feed.dto.request.RequestFeedUpdateDto;
import com.example.feedservice.feed.dto.response.ResponseFeedDto;
import com.example.feedservice.feed.dto.response.ResponseSuccessDto;
import com.example.feedservice.feed.dto.response.feed.FeedListDto;
import com.example.feedservice.feed.dto.response.member.ResponseMemberDto;
import com.example.feedservice.feed.dto.response.member.ResponseMemberProfileDto;
import com.example.feedservice.media.dto.MediaDto;
import com.example.feedservice.media.service.MediaService;
import com.example.feedservice.feed.dto.request.RequestFeedCreateDto;
import com.example.feedservice.feed.entity.FeedEntity;
import com.example.feedservice.feed.repository.FeedRepository;
import com.example.feedservice.common.util.FeedUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    private final MediaService mediaService;
    private final FeedUtil feedUtil;
    private final RedisTemplate<CursorDto, ResponseFeedDto> redisTemplate;

    @Value("${domain}")
    private String domain;

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
    public Mono<ResponseFeedDto> getFeedList(RequestFeedCursorDto requestFeedCursorDto) {

        LocalDateTime cursor = requestFeedCursorDto.getCursorDate();

        if(cursor == null){
            cursor = LocalDateTime.now();
        }

        try{
            // 커서(조회 시간)가 유효한지 체크 
            if(isValidLocalDateTime(cursor)) {
                ResponseFeedDto responseFeedDto = redisTemplate.opsForValue().get(requestFeedCursorDto);
                // Redis 체크 및 확인
                if(responseFeedDto != null){
                    Mono.just(responseFeedDto);
                } else {

                    // Feed Entity 조회 FetchJoin
                    List<FeedEntity> feedEntities = feedRepository.findFeedByCursor(cursor);

                    // Feed Entity 에서 memberId 추출
                    Set<String> memberIds = feedEntities.stream()
                            .map(FeedEntity::getMemberId)
                            .collect(Collectors.toSet());

                    // Set -> List
                    List<String> memberIdList = List.copyOf(memberIds);

                    return getMemberFromMemberService(memberIdList)
                            .map(memberFromMemberService -> {
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
                                .mediaDtos(feedEntity.getMediaList().stream().map(mediaEntity -> new MediaDto(mediaEntity.getMediaId(), mediaEntity.getMediaPath())).toList())
                                .reactionCount(feedEntity.getReactionList().size())         // 좋아요 수만
                                .build();
                                }).toList();

                                // DB에서 select 한 데이터 중 5개만 제외하고 나머지는 레디스에 저장, 제외한 데이터는 반환
                                return saveRedisAndReturnRemainingFeed(requestFeedCursorDto.getCursorDate(),collect);
                            });
                }
            }
        } catch(DateTimeParseException e){
            throw new IllegalArgumentException("Invalid cursor date");
        }

        throw new RuntimeException("Error");
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
     * member Service 에 비동기로 통신
     * */
    private Mono<List<ResponseMemberDto>> getMemberFromMemberService(List<String> memberIds){
        // URL 빌드
        WebClient webClient = WebClient.builder()
                .baseUrl(domain + ":8081")
                .build();

        return webClient.get()
                .uri("/getProfile/" + memberIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ResponseMemberDto>>() {});

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
