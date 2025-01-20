package com.pape.timetodo.domain.main.model.category;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateCategoryRS {

    private Long categoryIdx;

    private LocalDateTime updateDt;

}
