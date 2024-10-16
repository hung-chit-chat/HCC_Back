package com.example.feedservice.feed.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DateTimeException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FeedMonoServiceTest {

    @Autowired FeedService feedService;
    @Autowired FeedMonoService feedMonoService;

    @DisplayName("GetMemberFromMemberService 테스트")
    @Test
    public void testGetMemberFromMemberService(){

        
    }

    @DisplayName("CommunicateMemberService 테스트")
    @Test
    public void testCommunicateMemberService(){


    }


    @DisplayName("SaveRedisAndReturnRemainingFeed 테스트")
    @Test
    public void testSaveRedisAndReturnRemainingFeed(){


    }

}