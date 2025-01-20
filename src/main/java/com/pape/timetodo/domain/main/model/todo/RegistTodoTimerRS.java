package com.pape.timetodo.domain.main.model.todo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RegistTodoTimerRS {

    private List<Long> timerIdxList; // TimerHistoryIdx List

    private LocalDateTime updateDt;

}
