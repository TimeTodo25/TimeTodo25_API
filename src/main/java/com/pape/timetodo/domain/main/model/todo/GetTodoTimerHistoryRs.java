package com.pape.timetodo.domain.main.model.todo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class GetTodoTimerHistoryRs {

    private List<TimerHistory> timerHistories;

    @Data
    public static class TimerHistory{

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startDt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endDt;

        private LocalTime totalTm;

    }

}
