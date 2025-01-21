package com.pape.timetodo.domain.main.model.dday;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterDdayRQ {

    @NotNull
    @Schema(description = "디데이 내용", example = "퇴사", implementation = String.class)
    private String content;

    @NotNull
    @Schema(description = "디데이 지정일", example = "2024-12-30", implementation = LocalDate.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetDt;

    @NotNull
    @Schema(description = "삭제유무", example = "true", implementation = Boolean.class)
    private Boolean targetDelYn;
}
