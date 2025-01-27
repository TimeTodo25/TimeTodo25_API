package com.pape.timetodo.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusType {

    NORMAL('Y', "정상"),
    DELETED('D', "삭제"),
    END('E', "종료"),
    UPDATED('U', "개별수정"),
    ;

    private final Character value;
    private final String status;

}
