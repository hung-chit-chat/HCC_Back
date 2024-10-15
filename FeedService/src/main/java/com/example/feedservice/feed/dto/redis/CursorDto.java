package com.example.feedservice.feed.dto.redis;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CursorDto {

    private LocalDateTime cursorDate;

    @Builder
    public CursorDto(LocalDateTime cursorDate) {
        this.cursorDate = cursorDate;
    }
}
