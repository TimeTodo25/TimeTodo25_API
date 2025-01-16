package com.pape.timetodo.domain.main.model.category;

import com.pape.timetodo.domain.main.model.GetCategoryModel;
import lombok.Data;

import java.util.List;

@Data
public class MyCategoryRS {

    private List<GetCategoryModel> categoryList;
}
