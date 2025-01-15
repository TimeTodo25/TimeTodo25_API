package com.pape.timetodo.domain.main.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pape.timetodo.global.jpa.entity.CategoryEntity.PublicStatus;

import lombok.Data;

@Data
public class GetCategoryModel {

    private Long idx;

    private String title;

    private String mainColor;

    private PublicStatus publicStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDt;

    private List<GetTodoModel> todoList;

}
