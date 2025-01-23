package com.pape.timetodo.domain.main.model.routine;

import com.pape.timetodo.domain.main.model.GetRoutineModel;
import lombok.Data;

import java.util.List;

@Data
public class GetMyRoutineRS {

    private List<GetRoutineModel> routineList;
}
