package com.example.feedservice.feed.repository;

import com.example.feedservice.common.util.FeedUtil;
import com.example.feedservice.feed.entity.FeedEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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


    @DisplayName("게시글 조회 테스트 querydslTest")
    @Test
    @Transactional(readOnly = true)
    public void selectFeedQueryDslTest() throws Exception{

        List<FeedEntity> all = feedRepository.findFeedByCursor(LocalDateTime.now());

        for (FeedEntity feedEntity : all) {
            System.out.println("feedEntity = " + feedEntity.getFeedId());
            System.out.println("feedEntity = " + feedEntity.getMemberId());
            System.out.println("feedEntity = " + feedEntity.getMediaList().size());
        }

        /**
         * 테스트 완료 2024-10-16
         * */


    }

}