package com.pape.timetodo.domain.main.model.home;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HomeTodoModel {

    private Long idx;

    private String  content;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime todoTotalTm;
    
}
