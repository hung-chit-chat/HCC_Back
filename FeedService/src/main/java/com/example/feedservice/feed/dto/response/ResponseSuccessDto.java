package com.example.feedservice.feed.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseSuccessDto {

    private String status;

    @Builder
    public ResponseSuccessDto(String status) {
        this.status = status;
    }
}
