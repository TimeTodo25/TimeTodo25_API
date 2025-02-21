package com.pape.timetodo.domain.main.model.routine;

import com.pape.timetodo.domain.main.model.GetTodoModel;
import com.pape.timetodo.global.base.BaseUpdateRS;
import lombok.Data;

import java.util.List;

@Data
public class RegisterRoutineRS extends BaseUpdateRS {

    private Long idx; // Routine IDX

    private List<GetTodoModel> todoList;

}
