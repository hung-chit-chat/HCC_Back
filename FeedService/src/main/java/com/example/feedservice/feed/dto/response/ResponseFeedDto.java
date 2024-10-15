package com.example.feedservice.feed.dto.response;

import com.example.feedservice.feed.dto.response.feed.FeedListDto;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResponseFeedDto {

    private List<FeedListDto> feedListDto;

    // 이후 데이터가 있는지 없는지
    private boolean hasMore;

    @Builder
    public ResponseFeedDto(List<FeedListDto> feedListDto, boolean hasMore) {
        this.feedListDto = feedListDto;
        this.hasMore = hasMore;
    }
}
