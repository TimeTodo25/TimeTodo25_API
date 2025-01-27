package com.pape.timetodo.domain.main.model.routine;

import com.pape.timetodo.domain.main.model.GetCategoryRoutineModel;
import lombok.Data;

import java.util.List;

@Data
public class GetMyRoutineRS {

    private List<GetCategoryRoutineModel> categoryRoutineList;
}
