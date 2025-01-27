package com.pape.timetodo.domain.main.model.todo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class UpdateTodoRQ {

    @NotNull
    @Schema(description = "투두 IDX", example = "1", implementation = Long.class)
    private Long idx;

    @Schema(description = "투두 내용", example = "스웨거UI 문서화해주기", implementation = String.class)
    private String content;

    @Schema(description = "카테고리 IDX", example = "1", implementation = Long.class)
    private Long categoryIdx;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "투두 지정일", example = "[yyyy-MM-dd] 2024-10-30", implementation = LocalDate.class)
    private LocalDate targetDate;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(description = "투두 시작시간 [24시간 표시제]", example = "[HH:mm:ss] 09:00:00", implementation = LocalTime.class)
    private LocalTime startTargetTm;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(description = "투두 종료시간 [24시간 표시제]", example = "[HH:mm:ss] 11:00:00", implementation = LocalTime.class)
    private LocalTime endTargetTm;
}
