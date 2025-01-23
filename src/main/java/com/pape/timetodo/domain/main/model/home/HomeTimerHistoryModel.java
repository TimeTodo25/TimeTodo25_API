package com.pape.timetodo.domain.main.model.home;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HomeTimerHistoryModel {

    private String mainColor;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTm;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTm;

}
