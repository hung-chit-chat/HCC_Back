package com.example.feedservice.feed.dto.response;

import com.example.feedservice.feed.dto.response.feed.FeedDto;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResponseFeedDto {

    private List<FeedDto> feedDto = new ArrayList<>();

    // 이후 데이터가 있는지 없는지
    private boolean hssMore;

    @Builder
    public ResponseFeedDto(List<FeedDto> feedDto, boolean hssMore) {
        this.feedDto = feedDto;
        this.hssMore = hssMore;
    }
}
