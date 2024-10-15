package com.example.feedservice.feed.service;

import com.example.feedservice.feed.dto.redis.CursorDto;
import com.example.feedservice.feed.dto.request.RequestFeedCursorDto;
import com.example.feedservice.feed.dto.request.RequestFeedUpdateDto;
import com.example.feedservice.feed.dto.response.ResponseFeedDto;
import com.example.feedservice.feed.dto.response.feed.FeedListDto;
import com.example.feedservice.feed.dto.response.feed.ProjectionsFeedDto;
import com.example.feedservice.feed.dto.response.member.ResponseMemberDto;
import com.example.feedservice.feed.dto.response.member.ResponseMemberProfileDto;
import com.example.feedservice.feed.entity.FeedEntity;
import com.example.feedservice.feed.repository.FeedRepository;
import com.example.feedservice.media.service.MediaService;
import com.example.feedservice.feed.dto.request.RequestFeedCreateDto;
import com.example.feedservice.media.repository.MediaRepository;
import com.example.feedservice.common.util.FeedUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FeedServiceTest {

    @Autowired private FeedService feedService;
    @Autowired private FeedMonoService feedMonoService;
    @Autowired private FeedUtil feedUtil;
    @Autowired private MediaService mediaService;
    @Autowired private MediaRepository mediaRepository;

    @Autowired private RedisTemplate<CursorDto, ResponseFeedDto> redisTemplate;

    @Value("${upload.path}")
    private String uploadPath;
    @Autowired
    private FeedRepository feedRepository;


    @DisplayName("게시글 생성 테스트")
    @Test
    @Rollback(false)
    public void PostServiceTest() throws Exception{

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(new MockMultipartFile("testImage1",   "testImage1.jpeg", "image/jpeg", "t".getBytes(StandardCharsets.UTF_8)));
        multipartFiles.add(new MockMultipartFile("testImage2",  "testImage2.png", "image/png", "tg55555e".getBytes(StandardCharsets.UTF_8)));
        multipartFiles.add(new MockMultipartFile("testImage3",  "testImage3.jpg", "image/jpg", "test612312312312378Image".getBytes(StandardCharsets.UTF_8)));
        multipartFiles.add(new MockMultipartFile("testImage3",  "testImage3.jpg", "image/jpg", Files.readAllBytes(Paths.get(uploadPath + "/" + "test1.jpg"))));    // 테스트 완료
        multipartFiles.add(new MockMultipartFile("testImage5",  "testImage5.jpg", "image/jpg", Files.readAllBytes(Paths.get(uploadPath + "/" + "test1.jpg"))));    // 테스트 완료

        RequestFeedCreateDto requestFeedCreateDto = RequestFeedCreateDto.builder()
                .memberId("1234")
                .contents("5678")
                .publicScope("PUBLIC")
                .media(multipartFiles)
                .build();

        feedService.createFeed(requestFeedCreateDto);
        /**
         * 테스트 완료 2024-10-02
         * */
//        PostEntity post = postService.createPost(requestPostCreateDto);
//
//        List<FileEntity> fileEntities = fileRepository.findAllByPost(post);
//
//        Assertions.assertThat(fileEntities).hasSize(3);

    }

    @DisplayName("게시글 업데이트 테스트")
    @Test
    public void putTest() throws Exception{

        List<MultipartFile> multipartFiles = new ArrayList<>();

        //multipartFiles.add(new MockMultipartFile("testImage11515",   "testImage51212112.jpeg", "image/jpg", Files.readAllBytes(Paths.get(uploadPath + "/" + "test1.jpg"))));

        FeedEntity findFeed = feedRepository.findByMemberId("1234").get();

        RequestFeedUpdateDto requestFeedUpdateDto = RequestFeedUpdateDto.builder()
                .contents("5678")
                .publicScope("FRIEND")
                .media(multipartFiles)
                .build();

        feedService.updateFeed(findFeed.getFeedId(), requestFeedUpdateDto);

    }

    @DisplayName("localdatetime 커서 메소드 확인")
    @Test
    public void isValidLocalDateTime(){

        // 예외 확인
        Assertions.assertThatThrownBy(() -> {
            LocalDateTime.parse("2024-10-13A10:15:30"); // 잘못된 형식 (T 대신 A)
        })
                .isInstanceOf(DateTimeException.class);



        LocalDateTime validDateTime = LocalDateTime.parse("2024-10-13T10:15:30");

        boolean validLocalDateTime = feedService.isValidLocalDateTime(validDateTime);

        Assertions.assertThat(validLocalDateTime).isTrue();
    }

    static class memberdto{
        private String email;
        private String password;

        public memberdto(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    /**
     *                                              멤버 데이터 생성 및 피드 목록조회 확인
     * */
    @DisplayName("피드 목록 테스트 Mockito")
    @Test
    public void testGetFeedListMockTest(){

        LocalDateTime loca = LocalDateTime.now();
        CursorDto cursorDto = new CursorDto(loca);

        // Redis에서 캐시된 데이터가 없을 때 (null을 반환)
//        Mockito.when(redisTemplate.opsForValue().get(Mockito.eq(cursorDto)))
//                .thenReturn(null);

        ResponseMemberProfileDto responseMemberDto1 = new ResponseMemberProfileDto("1234", "C:/Users/User/Desktop/filetest/image1.jpg");
        ResponseMemberProfileDto responseMemberDto2 = new ResponseMemberProfileDto("4567", "C:/Users/User/Desktop/filetest/image2.jpg");
        ResponseMemberProfileDto responseMemberDto3 = new ResponseMemberProfileDto("abcd", "C:/Users/User/Desktop/filetest/image3.jpg");
        ResponseMemberProfileDto responseMemberDto4 = new ResponseMemberProfileDto("efgg", "C:/Users/User/Desktop/filetest/image4.jpg");

        // 가짜 Feed 데이터 설정
        List<FeedEntity> mockEntities = Arrays.asList(
                new FeedEntity(feedUtil.getUUID(), "1234", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(14)),
                new FeedEntity(feedUtil.getUUID(), "1234", "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(15)),
                new FeedEntity(feedUtil.getUUID(), "4567", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(16)),
                new FeedEntity(feedUtil.getUUID(), "4567", "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(17)),
                new FeedEntity(feedUtil.getUUID(), "abcd", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(18)),
                new FeedEntity(feedUtil.getUUID(), "abcd", "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(19)),
                new FeedEntity(feedUtil.getUUID(), "efgg", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(20)),
                new FeedEntity(feedUtil.getUUID(), "efgg", "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(21)),
                new FeedEntity(feedUtil.getUUID(), "1234", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(22)),
                new FeedEntity(feedUtil.getUUID(), "4567", "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(23)),
                new FeedEntity(feedUtil.getUUID(), "abcd", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(24)),
                new FeedEntity(feedUtil.getUUID(), "efgg", "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(25))
        );

        List<ProjectionsFeedDto> projectionsFeedDtos = new ArrayList<>();
        for (FeedEntity mockEntity : mockEntities) {
            ProjectionsFeedDto projectionsFeedDto = new ProjectionsFeedDto(mockEntity, 3, 0);
            projectionsFeedDtos.add(projectionsFeedDto);

        }

        // feedRepository를 mock 객체로 생성
        FeedRepository mockFeedRepository = Mockito.mock(FeedRepository.class);

        // Mockito 는 실제 객체에 접근 불가능
        Mockito.when(mockFeedRepository.findFeedByCursor(loca))
                .thenReturn(projectionsFeedDtos);

        // 가짜 Feed 데이터 설정
        List<FeedListDto> mockFeeds = Arrays.asList(
                new FeedListDto(feedUtil.getUUID(), responseMemberDto1, "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(14),  3, null, 0 ),
                new FeedListDto(feedUtil.getUUID(), responseMemberDto1, "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(15),  2, null, 0 ),
                new FeedListDto(feedUtil.getUUID(), responseMemberDto2, "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(16),  500, null, 0 ),
                new FeedListDto(feedUtil.getUUID(), responseMemberDto2, "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(17),  4, null, 0 ),
                new FeedListDto(feedUtil.getUUID(), responseMemberDto3, "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(18),  15, null, 0 ),
                new FeedListDto(feedUtil.getUUID(), responseMemberDto3, "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(19),  23, null, 0 ),
                new FeedListDto(feedUtil.getUUID(), responseMemberDto4, "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(20),  69, null, 0 ),
                new FeedListDto(feedUtil.getUUID(), responseMemberDto4, "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(21),  16, null, 0 ),
                new FeedListDto(feedUtil.getUUID(), responseMemberDto1, "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(22),  33, null, 0 ),
                new FeedListDto(feedUtil.getUUID(), responseMemberDto2, "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(23),  86, null, 0 ),
                new FeedListDto(feedUtil.getUUID(), responseMemberDto3, "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(24),  0, null, 0 ),
                new FeedListDto(feedUtil.getUUID(), responseMemberDto4, "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(25),  7, null, 0 )
        );

        // FeedMonoService에서 가짜 데이터 반환
        Mono<ResponseFeedDto> mockResponseFeedDto = Mono.just(new ResponseFeedDto(loca, mockFeeds, true));

        // feedMonoService mock 객체로 생성
        FeedMonoService mockMonoService = Mockito.mock(FeedMonoService.class);

        Mockito.when(mockMonoService.getMemberFromMemberService(Mockito.any(), Mockito.anyList(), Mockito.anyList()))
                .thenReturn(mockResponseFeedDto);

        RequestFeedCursorDto requestFeedCursorDto = new RequestFeedCursorDto(loca);

        // 테스트 실행
        // feedMonoService mock 객체로 생성
        FeedService mockService = Mockito.mock(FeedService.class);

        Mono<ResponseFeedDto> result = mockService.getFeedList(requestFeedCursorDto);
        ResponseFeedDto response = result.block();

        // 검증
        assertNotNull(response);  // 데이터가 반환되는지 확인
        assertEquals(12, response.getFeedListDto().size());  // 반환된 피드의 개수 검증
        assertEquals("TEST 1", response.getFeedListDto().get(0).getContents());  // 첫 번째 피드의 내용이 "TEST 1"인지 확인
        assertEquals("1234", response.getFeedListDto().get(0).getMember().getMemberId());  // 첫 번째 피드의 멤버 ID 검증
        assertTrue(response.isHasMore());  // 데이터가 더 있는지(hasMore 값이 true인지) 확인

    }

    @DisplayName("피드 목록 테스트")
    @Test
    public void testGetFeedListTest(){
        RequestFeedCursorDto requestFeedCursorDto = new RequestFeedCursorDto(LocalDateTime.now());
        feedService.getFeedList(requestFeedCursorDto);



    }



}