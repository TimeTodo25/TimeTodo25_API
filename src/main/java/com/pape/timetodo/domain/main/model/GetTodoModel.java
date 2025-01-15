package com.pape.timetodo.domain.main.model;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pape.timetodo.global.jpa.entity.RoutineEntity.CycleType;

import lombok.Data;

@Data
public class GetTodoModel {

    private Long idx;

    private String  content;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTargetTm;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTargetTm;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDt;

    private Boolean routineYn;

    private CycleType cycleType; // 반복 타입
    private String cycleValue; // 반복 값
    private String rm; // 비고란
    private LocalDate routineStartDt; // 루틴 시작일
    private LocalDate routineEndDt; // 루틴 종료일

    @JsonIgnore
    private Time dummyTodoTotalTm;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime todoTotalTm;

    public void setDummyTodoTotalTm(Time dummyTodoTotalTm){
        this.dummyTodoTotalTm = dummyTodoTotalTm;
        this.todoTotalTm = dummyTodoTotalTm.toLocalTime();
    }
    
}
