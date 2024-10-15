package com.example.feedservice.feed.repository.impl;

import com.example.feedservice.comment.entity.CommentEntity;
import com.example.feedservice.feed.dto.response.feed.ProjectionsFeedDto;
import com.example.feedservice.feed.entity.FeedEntity;
import com.example.feedservice.feed.repository.FeedRepositoryCustom;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.feedservice.comment.entity.QCommentEntity.commentEntity;
import static com.example.feedservice.feed.entity.QFeedEntity.feedEntity;
import static com.example.feedservice.media.entity.QMediaEntity.mediaEntity;
import static com.example.feedservice.reaction.entity.QReactionEntity.reactionEntity;

@RequiredArgsConstructor
public class FeedRepositoryCustomImpl implements FeedRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<ProjectionsFeedDto> findFeedByCursor(LocalDateTime cursor) {

        return queryFactory.select(Projections.constructor(ProjectionsFeedDto.class,
                        feedEntity,
                        commentEntity.count(),
                        reactionEntity.count()
                        ))
                .from(feedEntity)
                .leftJoin(feedEntity.commentList, commentEntity).fetchJoin()            // comment List FetchJoin
                .leftJoin(feedEntity.mediaList, mediaEntity)                // media List FetchJoin
                .leftJoin(feedEntity.reactionList, reactionEntity)          // reaction List FetchJoin
                .where(feedEntity.createdDate.before(cursor))
                .groupBy(feedEntity.feedId)
                .orderBy(feedEntity.createdDate.desc())                                 // 시간 역순
                .limit(15)                                                              // 15개
                .fetch();

    }
}
