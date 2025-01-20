package com.pape.timetodo.domain.main.model.todo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTodoRS {

    private Long todoIdx;

    private LocalDateTime updateDt;
}
