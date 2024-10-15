package com.example.feedservice.feed.repository;

import com.example.feedservice.feed.dto.response.feed.ProjectionsFeedDto;
import com.example.feedservice.feed.entity.FeedEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedRepositoryCustom {

    List<ProjectionsFeedDto> findFeedByCursor(LocalDateTime cursor);

}
