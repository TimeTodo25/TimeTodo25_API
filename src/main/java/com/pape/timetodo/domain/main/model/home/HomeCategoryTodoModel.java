package com.pape.timetodo.domain.main.model.home;

import com.pape.timetodo.global.jpa.entity.CategoryEntity;
import com.pape.timetodo.global.jpa.entity.TodoEntity;
import lombok.Data;

import java.util.List;

@Data
public class HomeCategoryTodoModel {

    private Long idx;

    private String title;

    private String mainColor;

    private CategoryEntity.PublicStatus publicStatus;

    private List<TodoEntity> todoList;
}
