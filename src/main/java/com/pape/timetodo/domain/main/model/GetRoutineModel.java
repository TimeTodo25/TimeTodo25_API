package com.pape.timetodo.domain.main.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pape.timetodo.global.jpa.entity.RoutineEntity.CycleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRoutineModel {

    private CycleType cycleType; // 반복 타입
    private String cycleValue; // 반복 값
    private String rm; // 비고란
    private LocalDate startDt; // 루틴 시작일
    private LocalDate endDt; // 루틴 종료일
}
