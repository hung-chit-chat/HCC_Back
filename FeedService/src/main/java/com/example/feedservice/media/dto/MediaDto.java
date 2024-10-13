package com.example.feedservice.media.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MediaDto {

    private String mediaId;

    private String mediaPath;

    @Builder
    public MediaDto(String mediaId, String mediaPath) {
        this.mediaId = mediaId;
        this.mediaPath = mediaPath;
    }
}
