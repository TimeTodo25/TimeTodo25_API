package com.pape.timetodo.domain.main.model.todo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterHomeRQ {

    @Schema(description = "날짜", example = "2025-05-24", implementation = LocalDate.class)
    LocalDate today;

    @Schema(description = "오늘의 목표", example = "완벽 클리어", implementation = String.class)
    String goal;

    @Schema(description = "오늘의 기분", example = "현재 기준: GOOD / SOSO / BAD", implementation = String.class)
    String mood;

}
