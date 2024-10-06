package com.example.feedservice.feed.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestFeedCreateDto {

    private String memberId;
    private String publicScope;
    private String contents;
    private List<MultipartFile> media;

    public RequestFeedCreateDto(RequestFeedCreateDto requestFeedCreateDto) {
    }

    @Builder
    public RequestFeedCreateDto(String memberId, String publicScope, String contents, List<MultipartFile> media) {
        this.memberId = memberId;
        this.publicScope = publicScope;
        this.contents = contents;
        this.media = media;
    }
}
