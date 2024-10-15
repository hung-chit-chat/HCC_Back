package com.example.feedservice.feed.dto.response.feed;

import com.example.feedservice.comment.dto.CommentDto;
import com.example.feedservice.feed.dto.response.member.ResponseMemberInfoDto;
import com.example.feedservice.media.dto.MediaDto;
import com.example.feedservice.reaction.dto.ReactionDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


/**
 * 피드 목록
 * */
@Getter
@NoArgsConstructor
public class FeedListDto {

    private String feedId;

    private ResponseMemberInfoDto member;

    private String publicScope;

    private String contents;

    private Integer commentCount;

    private List<MediaDto> mediaDtos = new ArrayList<>();

    private Integer reactionCount;


    @Builder
    public FeedListDto(String feedId, ResponseMemberInfoDto member, String publicScope, String contents, Integer commentCount, List<MediaDto> mediaDtos, Integer reactionCount) {
        this.feedId = feedId;
        this.member = member;
        this.publicScope = publicScope;
        this.contents = contents;
        this.commentCount = commentCount;
        this.mediaDtos = mediaDtos;
        this.reactionCount = reactionCount;
    }

    @Builder
    public FeedListDto(FeedListDto feedListDto, ResponseMemberInfoDto member, Integer commentCount, List<MediaDto> mediaDtos, Integer reactionCount) {
        this.feedId = feedListDto.getFeedId();
        this.publicScope = feedListDto.getPublicScope();
        this.contents = feedListDto.getContents();
        this.member = member;
        this.commentCount = commentCount;
        this.mediaDtos = mediaDtos;
        this.reactionCount = reactionCount;
    }
}
