package com.pape.timetodo.domain.main.model.category;

import java.time.LocalDateTime;

import com.pape.timetodo.global.jpa.entity.CategoryEntity.PublicStatus;

import lombok.Data;

@Data
public class UpdateCategoryRS {

    private Long idx;

    private String categoryTitle;

    private String mainColor;

    private PublicStatus publicStatus;

    private LocalDateTime createDt;

    private LocalDateTime updateDt;
}
