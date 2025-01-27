package com.pape.timetodo.domain.main.model;

import com.pape.timetodo.global.jpa.entity.CategoryEntity.PublicStatus;
import lombok.Data;

import java.util.List;

@Data
public class GetCategoryRoutineModel {

    private Long categoryIdx;

    private String title;

    private String mainColor;

    private PublicStatus publicStatus;

    private List<GetRoutineModel> routineList;

}
