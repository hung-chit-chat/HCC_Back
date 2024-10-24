package com.example.feedservice.feed.repository.impl;

import com.example.feedservice.feed.entity.FeedEntity;
import com.example.feedservice.feed.repository.FeedRepositoryCustom;
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
    public List<FeedEntity> findFeedByCursor(LocalDateTime cursor) {

        return queryFactory.select(feedEntity)
                .from(feedEntity)
                .leftJoin(feedEntity.mediaList, mediaEntity).fetchJoin()
                .leftJoin(feedEntity.commentList, commentEntity)
                .leftJoin(feedEntity.reactionList, reactionEntity)
                .where(feedEntity.createdDate.before(cursor))
                .orderBy(feedEntity.createdDate.desc())
                .limit(15)
                .fetch();

    }
}
