package com.pape.timetodo.domain.main.model.todo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;

@Data
public class GetTodoDetailRS {

    private Long idx;

    private String content;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime totalTm;

}
