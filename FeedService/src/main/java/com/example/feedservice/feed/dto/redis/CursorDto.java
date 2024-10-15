package com.example.feedservice.feed.dto.redis;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
public class CursorDto {

    private LocalDateTime cursorDate;

    @Builder
    public CursorDto(LocalDateTime cursorDate) {
        this.cursorDate = cursorDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CursorDto cursorDto = (CursorDto) o;
        return Objects.equals(cursorDate, cursorDto.cursorDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cursorDate);
    }
}
