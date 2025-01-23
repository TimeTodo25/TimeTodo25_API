package com.pape.timetodo.domain.main.model.routine;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pape.timetodo.domain.main.model.GetTodoModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RegisterRoutineRS {

    private Long idx; // Routine IDX

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDt;

    private List<GetTodoModel> todoList;

}
