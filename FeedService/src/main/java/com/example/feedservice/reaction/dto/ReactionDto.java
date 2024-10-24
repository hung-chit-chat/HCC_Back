package com.example.feedservice.reaction.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReactionDto {

    private String reactionId;

    @Builder
    public ReactionDto(String reactionId) {
        this.reactionId = reactionId;
    }
}
