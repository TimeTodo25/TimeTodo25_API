package com.pape.timetodo.domain.main.model.routhin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterRoutineRS {

    private Long idx; // Routine IDX

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDt;

    // private List<GetTodoModel> todoModelList;

}
