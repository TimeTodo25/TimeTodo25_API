package com.pape.timetodo.domain.main.model.home;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GetHomeRQ {

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "해당 지정일", example = "[yyyy-MM-dd] 2024-10-30", implementation = LocalDate.class)
    private LocalDate date;

}
