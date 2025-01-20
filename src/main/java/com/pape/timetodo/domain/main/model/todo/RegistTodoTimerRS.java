package com.pape.timetodo.domain.main.model.todo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RegistTodoTimerRS {

    private List<Long> timerIdxList; // TimerHistoryIdx List

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDt;

}
