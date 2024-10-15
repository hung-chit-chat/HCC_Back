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

        return queryFactory.selectFrom(feedEntity)
                .leftJoin(feedEntity.commentList, commentEntity).fetchJoin()            // comment List FetchJoin
                .leftJoin(feedEntity.mediaList, mediaEntity).fetchJoin()                // media List FetchJoin
                .leftJoin(feedEntity.reactionList, reactionEntity).fetchJoin()          // reaction List FetchJoin
                .where(feedEntity.createdDate.before(cursor))
                .orderBy(feedEntity.createdDate.desc())                                 // 시간 역순
                .limit(15)                                                              // 15개
                .fetch();

    }
}
