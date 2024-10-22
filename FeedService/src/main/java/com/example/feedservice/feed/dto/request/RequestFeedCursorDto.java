package com.example.feedservice.feed.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestFeedCursorDto {

    private LocalDateTime cursorDate;

    public RequestFeedCursorDto(LocalDateTime cursorDate) {
        this.cursorDate = cursorDate;
    }
}
