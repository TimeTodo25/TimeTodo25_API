package com.pape.timetodo.global.security;

import lombok.Getter;

@Getter
public enum LoggingType {


    LOGIN("LOGIN"), // 로그인
    LOGOUT("LOGOUT"), // 로그아웃

    LOGIN_WRONG_PW("LOGIN_WRONG_PW"), // 로그인이 잘못 되었음
    LOGIN_DISABLED("LOGIN_DISABLED"), // 계정이 비활성화 되어있음
    LOGIN_NOT_USER("LOGIN_NOT_USER"), // 유저를 찾을수 없음
    LOGIN_LOCKED("LOGIN_LOCKED"), // 계정이 잠겨있음
    LOGIN_EXPIRED("LOGIN_EXPIRED"), // 계정이 만료됨

    ;

    private String value;

    private LoggingType(String value){
        this.value = value;
    }
}
