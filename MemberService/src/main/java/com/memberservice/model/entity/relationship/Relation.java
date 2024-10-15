package com.memberservice.model.entity.relationship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Relation {

    FRIEND,
    BLOCK;

    public class Names {
        public static final String FRIEND = "FRIEND";
        public static final String BLOCK = "BLOCK";
    }

}
