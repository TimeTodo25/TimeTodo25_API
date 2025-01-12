package com.pape.timetodo.domain.main.model.todo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetHomeTodoRQ {

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "투두 지정일", example = "[yyyy-MM-dd] 2024-10-30", implementation = LocalDate.class)
    private LocalDate date;
}
