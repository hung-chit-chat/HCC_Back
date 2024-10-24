package com.example.feedservice.feed.dto.response.feed;

import com.example.feedservice.feed.dto.response.member.ResponseMemberProfileDto;
import com.example.feedservice.media.dto.MediaDto;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * 피드 목록
 * */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedListDto {

    private String feedId;

    private ResponseMemberProfileDto member;

    private String publicScope;

    private String contents;

    private Integer commentCount;

    private LocalDateTime createdDate;

    private List<MediaDto> mediaDtos = new ArrayList<>();

    private Integer reactionCount;

    private Boolean isLiked;


    @Builder
    public FeedListDto(String feedId, ResponseMemberProfileDto member, String publicScope, String contents, LocalDateTime createdDate, Integer commentCount, List<MediaDto> mediaDtos, Integer reactionCount, Boolean isLiked) {
        this.feedId = feedId;
        this.member = member;
        this.publicScope = publicScope;
        this.contents = contents;
        this.createdDate = createdDate;
        this.commentCount = commentCount;
        this.mediaDtos = mediaDtos;
        this.reactionCount = reactionCount;
        this.isLiked = isLiked;
    }

    @Builder
    public FeedListDto(FeedListDto feedListDto, ResponseMemberProfileDto member, Integer commentCount, List<MediaDto> mediaDtos, Integer reactionCount, Boolean isLiked) {
        this.feedId = feedListDto.getFeedId();
        this.publicScope = feedListDto.getPublicScope();
        this.contents = feedListDto.getContents();
        this.createdDate = feedListDto.getCreatedDate();
        this.member = member;
        this.commentCount = commentCount;
        this.mediaDtos = mediaDtos;
        this.reactionCount = reactionCount;
        this.isLiked = isLiked;
    }
}
