package com.pape.timetodo.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProgressStatus {
    _100(100),
    _50(50),
    _0(0),
    ;

    private final Integer value;
}
