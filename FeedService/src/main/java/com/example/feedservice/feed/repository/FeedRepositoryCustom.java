package com.example.feedservice.feed.repository;

import com.example.feedservice.feed.dto.response.feed.FeedDto;
import com.example.feedservice.feed.entity.FeedEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedRepositoryCustom {

    List<FeedEntity> findFeedByCursor(LocalDateTime cursor);

}
