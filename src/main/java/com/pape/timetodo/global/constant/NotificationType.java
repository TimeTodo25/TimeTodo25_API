package com.pape.timetodo.global.constant;

import lombok.Getter;

@Getter
public enum NotificationType {

    START_TODO_NOTI("시작시간 설정한 투두 알림 받기"),
    TARGET_DDAY_NOTI("디데이 당일 알림 받기"),
    FRIEND_NEW_TODO_NOTI("친구가 새로운 투두 등록시 알림 받기"),
    FRIEND_FOLLOW_NOTI("친구 신청 알림 받기"),
    ;

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }
}
