package com.pape.timetodo.domain.main.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class DdayTodoModel {

    private Long idx;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTargetDt;

    private Integer intervalDay;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDt;
}
