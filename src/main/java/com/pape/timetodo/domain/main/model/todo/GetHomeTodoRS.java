package com.pape.timetodo.domain.main.model.todo;

import java.util.List;

import com.pape.timetodo.domain.main.model.DdayTodoModel;
import com.pape.timetodo.domain.main.model.GetCategoryModel;

import lombok.Data;

@Data
public class GetHomeTodoRS {

    private List<DdayTodoModel> intervalDayTodoList;

    private List<GetCategoryModel> categoryList;

}
