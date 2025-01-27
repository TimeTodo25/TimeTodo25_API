package com.pape.timetodo.domain.main.model.routine;

import com.pape.timetodo.global.jpa.entity.RoutineEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class GetRoutineDetailRS {

    private Long idx;

    private String content;

    private String rm;

    private LocalDate startDt;

    private LocalDate endDt;

    private LocalTime startTm;

    private LocalTime endTm;

    private RoutineEntity.CycleType cycleType;

    private String cycleValue;
}
