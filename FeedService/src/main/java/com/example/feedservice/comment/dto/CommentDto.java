package com.example.feedservice.comment.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentDto {

    private String commentId;

    private String contents;

    @Builder
    public CommentDto(String commentId, String contents) {
        this.commentId = commentId;
        this.contents = contents;
    }
}
