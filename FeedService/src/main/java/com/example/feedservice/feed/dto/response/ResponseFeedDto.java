package com.example.feedservice.feed.dto.response;

import com.example.feedservice.feed.dto.response.feed.FeedListDto;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseFeedDto {

    private String cursorDate;
    private String nextCursorDate;

    private List<FeedListDto> feedListDto;

    // 이후 데이터가 있는지 없는지
    private boolean hasMore;

    @Builder
    public ResponseFeedDto(String cursorDate, String nextCursorDate, List<FeedListDto> feedListDto, boolean hasMore) {
        this.cursorDate = cursorDate;
        this.nextCursorDate = nextCursorDate;
        this.feedListDto = feedListDto;
        this.hasMore = hasMore;
    }
}
