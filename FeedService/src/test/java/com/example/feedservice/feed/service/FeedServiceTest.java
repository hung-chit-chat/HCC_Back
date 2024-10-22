package com.example.feedservice.feed.service;

import com.example.feedservice.feed.dto.redis.CursorDto;
import com.example.feedservice.feed.dto.request.RequestFeedUpdateDto;
import com.example.feedservice.feed.dto.response.ResponseFeedDto;
import com.example.feedservice.feed.entity.FeedEntity;
import com.example.feedservice.feed.repository.FeedRepository;
import com.example.feedservice.media.service.MediaService;
import com.example.feedservice.feed.dto.request.RequestFeedCreateDto;
import com.example.feedservice.media.repository.MediaRepository;
import com.example.feedservice.common.util.FeedUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class FeedServiceTest {

    @InjectMocks private FeedService feedService;
    @InjectMocks private FeedMonoService feedMonoService;
    @Mock private FeedUtil feedUtil;
    @Mock private MediaService mediaService;
    @Mock private MediaRepository mediaRepository;

    @Mock
    @Qualifier("feedListRedisTemplate")
    private RedisTemplate<CursorDto, ResponseFeedDto> redisTemplate;

    @Value("${upload.path}")
    private String uploadPath;
    @Mock
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



        String validDateTime = LocalDateTime.parse("2024-10-13T10:15:30").toString();

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


}