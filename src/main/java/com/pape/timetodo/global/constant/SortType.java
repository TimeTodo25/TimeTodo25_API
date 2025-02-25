package com.pape.timetodo.global.constant;

import lombok.Getter;

@Getter
public enum SortType {

    // D_REGISTRATION_ORDER("디데이 등록한 순으로 정렬"),
    D_COMPLETE_ORDER("D 완료된 Dday 뒤로 정렬"),
    D_SWIPE_CHECK("D 스와이프로 체크"),
    C_REGISTRATION_ORDER("C 등록한 순으로 정렬"),
    C_COMPLETE_ORDER("C 완료된 Todo 뒤로 정렬"),
    ;

    private final String description;

    SortType(String description) {
        this.description = description;
    }

}
