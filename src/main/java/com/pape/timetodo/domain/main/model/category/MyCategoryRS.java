package com.pape.timetodo.domain.main.model.category;

import java.util.List;

import com.pape.timetodo.domain.main.model.GetCategoryModel;

import lombok.Data;

@Data
public class MyCategoryRS {

    private List<GetCategoryModel> categoryList;
}
