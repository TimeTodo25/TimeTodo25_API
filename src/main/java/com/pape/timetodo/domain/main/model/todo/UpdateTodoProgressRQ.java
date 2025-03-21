package com.pape.timetodo.domain.main.model.todo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTodoProgressRQ {

    @NotNull
    @Schema(description = "투두 IDX", example = "1", implementation = Long.class)
    private Long idx;

    @NotNull
    @Schema(description = "진행도 - 0, 50, 100만 허용", example = "50", implementation = Integer.class)
    private Integer progressStatus;

}
