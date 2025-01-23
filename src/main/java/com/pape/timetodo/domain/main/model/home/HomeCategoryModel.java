package com.pape.timetodo.domain.main.model.home;

import com.pape.timetodo.global.jpa.entity.CategoryEntity.PublicStatus;
import lombok.Data;

import java.util.List;

@Data
public class HomeCategoryModel {

    private Long idx;

    private String title;

    private String mainColor;

    private PublicStatus publicStatus;

    private List<HomeTodoModel> todoList;

}
