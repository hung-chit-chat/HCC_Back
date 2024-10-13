package com.example.feedservice.feed.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequestFeedCursorDto {

    private LocalDateTime cursorDate;

    public RequestFeedCursorDto(LocalDateTime cursorDate) {
        this.cursorDate = cursorDate;
    }
}
