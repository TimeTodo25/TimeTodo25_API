package com.pape.timetodo.domain.main.model.routine;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pape.timetodo.global.jpa.entity.RoutineEntity.CycleType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class RegisterRoutineRQ {

    @Schema(description = "투두 IDX", example = "1", implementation = Long.class)
    private Long todoIdx;

    @NotNull
    @Schema(description = "투두 내용", example = "스웨거UI 문서화해주기", implementation = String.class)
    private String content;

    @NotNull
    @Schema(description = "카테고리 IDX", example = "1", implementation = Long.class)
    private Long categoryIdx;

    @NotNull
    @Schema(description = "반복타입[EVERY_DAY ,EVERY_WEEK, EVERY_MONTH]", example = "EVERY_WEEK", implementation = CycleType.class)
    private CycleType cycleType;

    @ArraySchema(schema = @Schema(description = "반복값[EVERY_DAY : value없음 , EVERY_WEEK: 1~7(1: 월요일, 2: 화요일 ... 7: 일요일), EVERY_MONTH : 1 ~ 31]", example = "1", implementation = Integer.class))
    private List<Byte> cycleValue; // 반복주기

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "루틴 시작일", example = "2024-11-01", implementation = LocalDate.class)
    private LocalDate startDt; // 루틴 시작일

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "루틴 종료일", example = "2024-11-15", implementation = LocalDate.class)
    private LocalDate endDt; // 루틴 끝일

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(description = "투두 시작시간 [24시간 표시제]", example = "[HH:mm:ss] 09:00:00", implementation = LocalTime.class)
    private LocalTime startTargetTm;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(description = "투두 종료시간 [24시간 표시제]", example = "[HH:mm:ss] 11:00:00", implementation = LocalTime.class)
    private LocalTime endTargetTm;

}
