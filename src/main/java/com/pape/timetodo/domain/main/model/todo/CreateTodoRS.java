package com.pape.timetodo.domain.main.model.todo;

import com.pape.timetodo.global.base.BaseUpdateRS;
import lombok.Data;

@Data
public class CreateTodoRS extends BaseUpdateRS {

    private Long todoIdx;

}
