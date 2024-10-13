package com.example.feedservice.feed.repository.impl;

import com.example.feedservice.comment.dto.CommentDto;
import com.example.feedservice.comment.entity.QCommentEntity;
import com.example.feedservice.feed.dto.response.feed.FeedDto;
import com.example.feedservice.feed.entity.FeedEntity;
import com.example.feedservice.feed.entity.QFeedEntity;
import com.example.feedservice.feed.repository.FeedRepositoryCustom;
import com.example.feedservice.media.dto.MediaDto;
import com.example.feedservice.media.entity.QMediaEntity;
import com.example.feedservice.reaction.dto.ReactionDto;
import com.example.feedservice.reaction.entity.QReactionEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .limit(15)                                                              // 15개
                .fetch();


//        return Optional.of(fetchData.stream()
//                .map(feed -> FeedDto.builder()
//                        .feedId(feed.getFeedId())
//                        .publicScope(feed.getPublicScope())
//                        .contents(feed.getContents())
//                        // comment List 변환 후 DTO 매핑
//                        .commentDtos(feed.getCommentList().stream()
//                                .map(comment -> new CommentDto(comment.getCommentId(), comment.getContents()))
//                                .collect(Collectors.toList()))
//                        // media List 변환 후 DTO 매핑
//                        .mediaDtos(feed.getMediaList().stream()
//                                .map(media -> new MediaDto(media.getMediaId(), media.getMediaPath()))
//                                .collect(Collectors.toList()))
//                        // reaction List 변환 후 DTO 매핑
//                        .reactionDtos(feed.getReactionList().stream()
//                                .map(reaction -> new ReactionDto(reaction.getReactionId()))
//                                .collect(Collectors.toList()))
//                        .build())
//                .collect(Collectors.toList()));
    }
}
