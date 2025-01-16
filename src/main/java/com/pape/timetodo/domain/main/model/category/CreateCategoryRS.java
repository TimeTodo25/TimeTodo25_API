package com.pape.timetodo.domain.main.model.category;

import com.pape.timetodo.global.jpa.entity.CategoryEntity.PublicStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateCategoryRS {

    private Long categoryIdx;

    private String categoryTitle;

    private String mainColor;

    private PublicStatus publicStatus;

    private LocalDateTime createDt;

    private LocalDateTime updateDt;

}
