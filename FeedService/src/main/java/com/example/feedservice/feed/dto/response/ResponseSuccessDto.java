package com.example.feedservice.feed.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseSuccessDto {

    private String result;

    @Builder
    public ResponseSuccessDto(String result) {
        this.result = result;
    }
}
