package com.pape.timetodo.domain.main.model.category;

import com.pape.timetodo.global.jpa.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetCategoryDetailRS {

    private String title;

    private String mainColor;

    private CategoryEntity.PublicStatus publicStatus;

    private List<Long> todoIdxList;
}