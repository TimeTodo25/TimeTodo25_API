package com.pape.timetodo.global.constant;

import lombok.Getter;

@Getter
public enum TermsType {

    SERVICE_POLICY("서비스 이용약관 (필수)"),
    PRIVACY_POLICY("개인정보 수집 및 이용약관 (필수)"),
    MARKETING_POLICY("광고 및 마케팅 수신 (선택) = option terms"),
    ;

    private final String description;

    TermsType(String description) {
        this.description = description;
    }

}
