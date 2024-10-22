package com.example.feedservice.feed.service;

import com.example.feedservice.common.util.FeedUtil;
import com.example.feedservice.feed.dto.redis.CursorDto;
import com.example.feedservice.feed.dto.request.RequestFeedCursorDto;
import com.example.feedservice.feed.dto.response.ResponseFeedDto;
import com.example.feedservice.feed.dto.response.feed.FeedListDto;
import com.example.feedservice.feed.dto.response.member.ResponseMemberDto;
import com.example.feedservice.feed.dto.response.member.ResponseMemberProfileDto;
import com.example.feedservice.feed.entity.FeedEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import reactor.core.publisher.Mono;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FeedMonoServiceTest {

    @Autowired FeedService feedService;
    @Autowired FeedMonoService feedMonoService;
    @Autowired FeedUtil feedUtil;

    @Mock FeedRestService feedRestService;

    @Autowired RedisTemplate<String, ResponseFeedDto> redisTemplate;

    @DisplayName("GetMemberFromMemberService 테스트")
    @Test
    public void testGetMemberFromMemberService(){
        // 임의의 memberIds 설정
        List<String> memberIds = Arrays.asList("1111", "2222");

        ResponseMemberDto testUser3 = new ResponseMemberDto("28a1a3fc-be61-4638-ae8c-a9a78af6dea5", "ccc@ccc.com", "테스트유저3", "1", null, null);
        ResponseMemberDto testUser6 = new ResponseMemberDto("51ddc9b0-c074-46ca-a01d-1551afecccfa", "fff@fff.com", "테스트유저6", "0", null, null);
        ResponseMemberDto testUser4 = new ResponseMemberDto("6610df19-d9c7-4536-9a1e-b7031782de58", "ddd@ddd.com", "테스트유저4", "0", null, null);

        List<ResponseMemberDto> members = new ArrayList<>();
        members.add(testUser3);
        members.add(testUser6);
        members.add(testUser4);

        // feedRestService의 Mock 설정 - Mono로 감싸서 반환
        Mockito.when(feedRestService.communicateMemberService(memberIds)).thenReturn(Mono.just(members));

        LocalDateTime loca = LocalDateTime.now();
        RequestFeedCursorDto requestFeedCursorDto = new RequestFeedCursorDto(loca);

        // 가짜 Feed 데이터 설정
        List<FeedEntity> mockEntities = Arrays.asList(
                new FeedEntity(feedUtil.getUUID(), "28a1a3fc-be61-4638-ae8c-a9a78af6dea5", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(14)),
                new FeedEntity(feedUtil.getUUID(), "28a1a3fc-be61-4638-ae8c-a9a78af6dea5", "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(15)),
                new FeedEntity(feedUtil.getUUID(), "28a1a3fc-be61-4638-ae8c-a9a78af6dea5", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(16)),
                new FeedEntity(feedUtil.getUUID(), "28a1a3fc-be61-4638-ae8c-a9a78af6dea5", "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(17)),
                new FeedEntity(feedUtil.getUUID(), "51ddc9b0-c074-46ca-a01d-1551afecccfa", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(18)),
                new FeedEntity(feedUtil.getUUID(), "51ddc9b0-c074-46ca-a01d-1551afecccfa", "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(19)),
                new FeedEntity(feedUtil.getUUID(), "6610df19-d9c7-4536-9a1e-b7031782de58", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(20)),
                new FeedEntity(feedUtil.getUUID(), "6610df19-d9c7-4536-9a1e-b7031782de58", "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(21)),
                new FeedEntity(feedUtil.getUUID(), "28a1a3fc-be61-4638-ae8c-a9a78af6dea5", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(22)),
                new FeedEntity(feedUtil.getUUID(), "51ddc9b0-c074-46ca-a01d-1551afecccfa", "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(23)),
                new FeedEntity(feedUtil.getUUID(), "6610df19-d9c7-4536-9a1e-b7031782de58", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(24)),
                new FeedEntity(feedUtil.getUUID(), "6610df19-d9c7-4536-9a1e-b7031782de58", "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(25))
        );

        Mono<ResponseFeedDto> memberFromMemberService = feedMonoService.getMemberFromMemberService(requestFeedCursorDto, memberIds, mockEntities);
        ResponseFeedDto block = memberFromMemberService.block();

        System.out.println("block.getCursorDate() = " + block.getCursorDate());
        System.out.println("block.getCursorDate() = " + block.getNextCursorDate());
        System.out.println("block.getCursorDate() = " + block.getFeedListDto().size());




    }

    @DisplayName("CommunicateMemberService 테스트")
    @Test
    public void testCommunicateMemberService(){

        // 임의의 memberIds 설정
        List<String> memberIds = Arrays.asList("1111", "2222");

        ResponseMemberDto testUser3 = new ResponseMemberDto("28a1a3fc-be61-4638-ae8c-a9a78af6dea5", "ccc@ccc.com", "테스트유저3", "1", null, null);
        ResponseMemberDto testUser6 = new ResponseMemberDto("51ddc9b0-c074-46ca-a01d-1551afecccfa", "fff@fff.com", "테스트유저6", "0", null, null);
        ResponseMemberDto testUser4 = new ResponseMemberDto("6610df19-d9c7-4536-9a1e-b7031782de58", "ddd@ddd.com", "테스트유저4", "0", null, null);

        List<ResponseMemberDto> members = new ArrayList<>();
        members.add(testUser3);
        members.add(testUser6);
        members.add(testUser4);

        // feedRestService의 Mock 설정 - Mono로 감싸서 반환
        Mockito.when(feedRestService.communicateMemberService(memberIds)).thenReturn(Mono.just(members));



    }


    @DisplayName("SaveRedisAndReturnRemainingFeed 테스트")
    @Test
    public void testSaveRedisAndReturnRemainingFeed(){

        // 임의의 memberIds 설정
        List<String> memberIds = Arrays.asList("1111", "2222");

        ResponseMemberDto testUser3 = new ResponseMemberDto("28a1a3fc-be61-4638-ae8c-a9a78af6dea5", "ccc@ccc.com", "테스트유저3", "1", null, null);
        ResponseMemberDto testUser6 = new ResponseMemberDto("51ddc9b0-c074-46ca-a01d-1551afecccfa", "fff@fff.com", "테스트유저6", "0", null, null);
        ResponseMemberDto testUser4 = new ResponseMemberDto("6610df19-d9c7-4536-9a1e-b7031782de58", "ddd@ddd.com", "테스트유저4", "0", null, null);

        List<ResponseMemberDto> members = new ArrayList<>();
        members.add(testUser3);
        members.add(testUser6);
        members.add(testUser4);

        // feedRestService의 Mock 설정 - Mono로 감싸서 반환
        Mockito.when(feedRestService.communicateMemberService(memberIds)).thenReturn(Mono.just(members));

        LocalDateTime loca = LocalDateTime.now();

        ResponseMemberProfileDto testUserDto3 = new ResponseMemberProfileDto(testUser3.getMemberId(), null);
        ResponseMemberProfileDto testUserDto4 = new ResponseMemberProfileDto(testUser4.getMemberId(), null);
        ResponseMemberProfileDto testUserDto6 = new ResponseMemberProfileDto(testUser6.getMemberId(), null);

        // 가짜 Feed 데이터 설정
        List<FeedListDto> mockFeeds = Arrays.asList(
                new FeedListDto(feedUtil.getUUID(), testUserDto3, "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(15),  2, null, 0 , false),
                new FeedListDto(feedUtil.getUUID(), testUserDto4, "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(16),  500, null, 0 ,false),
                new FeedListDto(feedUtil.getUUID(), testUserDto3, "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(17),  4, null, 0 ,false),
                new FeedListDto(feedUtil.getUUID(), testUserDto4, "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(18),  15, null, 0 ,false),
                new FeedListDto(feedUtil.getUUID(), testUserDto4, "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(19),  23, null, 0 ,false),
                new FeedListDto(feedUtil.getUUID(), testUserDto6, "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(20),  69, null, 0 ,false),
                new FeedListDto(feedUtil.getUUID(), testUserDto6, "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(21),  16, null, 0 ,false),
                new FeedListDto(feedUtil.getUUID(), testUserDto4, "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(22),  33, null, 0 ,false),
                new FeedListDto(feedUtil.getUUID(), testUserDto4, "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(23),  86, null, 0 ,false),
                new FeedListDto(feedUtil.getUUID(), testUserDto4, "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(24),  0, null, 0 ,false),
                new FeedListDto(feedUtil.getUUID(), testUserDto4, "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(25),  7, null, 0 ,false),
                new FeedListDto(feedUtil.getUUID(), testUserDto3, "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(14),  3, null, 0 ,false)
        );

        List<FeedListDto> sortedList = mockFeeds.stream().sorted(Comparator.comparing(FeedListDto::getCreatedDate).reversed()).toList();

        // 첫 5개 데이터
        ResponseFeedDto responseFeedDto = feedMonoService.saveRedisAndReturnRemainingFeed(loca, sortedList);

        Assertions.assertThat(responseFeedDto).isNotNull();
        Assertions.assertThat(responseFeedDto.getFeedListDto().size()).isEqualTo(5);
        Assertions.assertThat(responseFeedDto.getCursorDate()).isEqualTo(loca.toString());
        Assertions.assertThat(responseFeedDto.getNextCursorDate()).isNotNull();
        Assertions.assertThat(responseFeedDto.isHasMore()).isTrue();

        /**
         * 여기까지 테스트 완료
         * */

        // 두번째 5개 데이터
        ResponseFeedDto redisFeedDto = redisTemplate.opsForValue().get(responseFeedDto.getNextCursorDate());
        Assertions.assertThat(redisFeedDto.getFeedListDto().size()).isEqualTo(5);
        assert redisFeedDto != null;
        Assertions.assertThat(redisFeedDto.isHasMore()).isTrue();

        // 세번재 2개 데이터
        ResponseFeedDto redisFeedDtoNext = redisTemplate.opsForValue().get(redisFeedDto.getNextCursorDate());
        Assertions.assertThat(redisFeedDtoNext.getFeedListDto().size()).isEqualTo(2);
        assert redisFeedDtoNext != null;
        Assertions.assertThat(redisFeedDtoNext.isHasMore()).isFalse();


        /**
         * 테스트 완료
         * */



    }

}