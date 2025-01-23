package com.pape.timetodo.domain.main.model;

import com.pape.timetodo.global.jpa.entity.RoutineEntity.CycleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRoutineModel {

    private Long idx;

    private CycleType cycleType; // 반복 타입

    private String cycleValue; // 반복 값

    private String rm; // 비고란

    private LocalDate startDt; // 루틴 시작일

    private LocalDate endDt; // 루틴 종료일
}
