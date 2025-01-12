package com.pape.timetodo.domain.main.model.todo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class GetTodoDetailRS {

    private Long idx;

    private String content;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime totalTm;

    private List<TimerHistory> timerHistories;

    @Data
    public static class TimerHistory{

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startDt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endDT;

        private LocalTime totalTm;

    }
}
