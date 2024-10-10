package com.memberservice.service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Phone {
    @NotNull
    String number;
}
