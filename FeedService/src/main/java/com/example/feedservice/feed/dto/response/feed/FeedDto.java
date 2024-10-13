package com.example.feedservice.feed.dto.response.feed;

import com.example.feedservice.comment.dto.CommentDto;
import com.example.feedservice.feed.dto.response.member.ResponseMemberInfoDto;
import com.example.feedservice.media.dto.MediaDto;
import com.example.feedservice.reaction.dto.ReactionDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class FeedDto {

    private String feedId;

    private ResponseMemberInfoDto member;

    private String publicScope;

    private String contents;

    private List<CommentDto> commentDtos = new ArrayList<>();

    private List<MediaDto> mediaDtos = new ArrayList<>();

    private List<ReactionDto> reactionDtos = new ArrayList<>();

    @Builder
    public FeedDto(String feedId, ResponseMemberInfoDto member, String publicScope, String contents, List<CommentDto> commentDtos, List<MediaDto> mediaDtos, List<ReactionDto> reactionDtos) {
        this.feedId = feedId;
        this.member = member;
        this.publicScope = publicScope;
        this.contents = contents;
        this.commentDtos = commentDtos;
        this.mediaDtos = mediaDtos;
        this.reactionDtos = reactionDtos;
    }
}
