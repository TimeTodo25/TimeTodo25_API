package com.pape.timetodo.domain.main.model.todo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CreateTodoRS {

    private String content;

    private String categoryTitle;

    private LocalDateTime createDt;

    private LocalDateTime updateDt;
}
