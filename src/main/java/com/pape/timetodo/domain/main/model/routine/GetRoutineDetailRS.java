package com.pape.timetodo.domain.main.model.routine;

import com.pape.timetodo.domain.main.model.GetTodoModel;
import com.pape.timetodo.global.jpa.entity.RoutineEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class GetRoutineDetailRS {

    private Long idx;

    private RoutineEntity.CycleType cycleType;

    private String cycleValue;

    private String rm;

    private LocalDate startDt;

    private LocalDate endDt;

    private List<GetTodoModel> todoList; // 이거 그냥 idx는 안되나??
}
