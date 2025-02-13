package com.pape.timetodo.domain.main.model.todo;

import com.pape.timetodo.global.base.BaseUpdateRS;
import lombok.Data;

import java.util.List;

@Data
public class RegistTodoTimerRS extends BaseUpdateRS {

    private List<Long> timerIdxList; // TimerHistoryIdx List

}
