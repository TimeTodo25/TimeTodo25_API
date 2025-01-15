package com.pape.timetodo.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DayWeekType {

    _1(1, "월요일"),
    _2(2, "화요일"),
    _3(3, "수요일"),
    _4(4, "목요일"),
    _5(5, "금요일"),
    _6(6, "토요일"),
    _7(7, "일요일"),
    ;

    private final Integer value;
    private final String desc;

    public static DayWeekType fromValue(Integer value){

        for(DayWeekType dayWeekType : DayWeekType.values()){
            if(dayWeekType.getValue().equals(value)) return dayWeekType;   
        }

        return null;
    }
}
