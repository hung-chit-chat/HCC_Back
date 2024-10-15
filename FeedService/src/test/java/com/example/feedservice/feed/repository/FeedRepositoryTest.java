package com.example.feedservice.feed.repository;

import com.example.feedservice.common.util.FeedUtil;
import com.example.feedservice.feed.dto.request.RequestFeedUpdateDto;
import com.example.feedservice.feed.dto.response.feed.ProjectionsFeedDto;
import com.example.feedservice.feed.entity.FeedEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FeedRepositoryTest {

    @Autowired private FeedRepository feedRepository;
    @Autowired private FeedUtil feedUtil;

//    @BeforeEach
    @Transactional
    void setFeed() {

        LocalDateTime loca = LocalDateTime.now();

        // 가짜 Feed 데이터 설정
        List<FeedEntity> mockEntities = Arrays.asList(
                new FeedEntity(feedUtil.getUUID(), "1234", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(14)),
                new FeedEntity(feedUtil.getUUID(), "1234", "PUBLIC", "TEST 1",loca.minusHours(2).plusMinutes(15)),
                new FeedEntity(feedUtil.getUUID(), "4567", "PUBLIC", "TEST 1",loca.minusHours(1).plusMinutes(16))
        );

        feedRepository.saveAll(mockEntities);
        feedRepository.flush();
    }

    @DisplayName("게시글 조회 테스트")
    @Test
    @Transactional(readOnly = true)
    public void selectFeed() throws Exception{

        List<FeedEntity> all = feedRepository.findAll();

        Assertions.assertThat(all.size()).isEqualTo(3);

    }


    @DisplayName("게시글 조회 테스트 querydsl")
    @Test
    @Transactional(readOnly = true)
    public void selectFeedQueryDsl() throws Exception{

        List<ProjectionsFeedDto> all = feedRepository.findFeedByCursor(LocalDateTime.now());

        for (ProjectionsFeedDto projectionsFeedDto : all) {
            System.out.println("projectionsFeedDto = " + projectionsFeedDto.getFeedEntity().getFeedId());
            System.out.println("projectionsFeedDto = " + projectionsFeedDto.getFeedEntity().getMemberId());
            System.out.println("projectionsFeedDto = " + projectionsFeedDto.getFeedEntity().getMediaList().size());
            System.out.println("projectionsFeedDto = " + projectionsFeedDto.getFeedEntity().getMediaList().get(0).getMediaPath());
            System.out.println("projectionsFeedDto = " + projectionsFeedDto.getCommentCount());
            System.out.println("projectionsFeedDto = " + projectionsFeedDto.getReactionCount());
        }

        /**
         * 테스트 완료 2024-10-15
         *
         * feed에 등록된 파일까지 확인 완료
         * projectionsFeedDto = 5
         * projectionsFeedDto = C:\Users\User\Desktop\filetest\2aab9d06-6f47-415e-a79e-fd18a8adb719/71cfe7bc-75a6-497b-a5af-3b08fb2bcba9.png
         * projectionsFeedDto = 0
         * projectionsFeedDto = 0
         * */

    }

}