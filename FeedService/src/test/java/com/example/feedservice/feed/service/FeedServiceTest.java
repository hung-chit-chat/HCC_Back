package com.example.feedservice.feed.service;

import com.example.feedservice.feed.dto.request.RequestFeedUpdateDto;
import com.example.feedservice.feed.entity.FeedEntity;
import com.example.feedservice.feed.repository.FeedRepository;
import com.example.feedservice.media.service.MediaService;
import com.example.feedservice.feed.dto.request.RequestFeedCreateDto;
import com.example.feedservice.media.repository.MediaRepository;
import com.example.feedservice.common.util.FeedUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class FeedServiceTest {

    @Autowired private FeedService feedService;
    @Autowired private FeedUtil feedUtil;
    @Autowired private MediaService mediaService;
    @Autowired private MediaRepository mediaRepository;

    @Value("${upload.path}")
    private String uploadPath;
    @Autowired
    private FeedRepository feedRepository;


    @DisplayName("게시글 생성 테스트")
    @Test
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
}