package com.example.feedservice.feed.dto.response.feed;

import com.example.feedservice.feed.entity.FeedEntity;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectionsFeedDto {

    private FeedEntity feedEntity;

    private long commentCount;

    private long reactionCount;

    @QueryProjection
    public ProjectionsFeedDto(FeedEntity feedEntity, long commentCount, long reactionCount) {
        this.feedEntity = feedEntity;
        this.commentCount = commentCount;
        this.reactionCount = reactionCount;
    }
}
