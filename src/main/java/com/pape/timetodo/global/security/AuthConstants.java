package com.pape.timetodo.global.security;

import lombok.Getter;

@Getter
public enum AuthConstants {
    // 로그인 정보들
    LOGIN_INFO_SESSION("loginRQ"),

    // JWT 토큰
    AUTH_HEADER("Authorization"),
    TOKEN_TYPE("Bearer"),

    // 문자메세지
    REG_CERTIFICATE("REG_CERTIFICATE"), // 회원가입 용 인증메세지
    ;

    private String value;

    private AuthConstants (String value){
        this.value = value;
    }
}
