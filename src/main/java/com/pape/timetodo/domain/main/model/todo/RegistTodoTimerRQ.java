package com.pape.timetodo.domain.main.model.todo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RegistTodoTimerRQ {

    @NotNull
    @Schema(description = "투두 IDX", example = "1", implementation = Long.class)
    private Long todoIdx;

    @ArraySchema(schema = @Schema(implementation = TimeData.class))
    private List<TimeData> timeDatas;

    @Data
    public static class TimeData{
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "투두 스톱워치 시작 시간데이터", example = "2024-10-24 09:00:00", implementation = LocalDateTime.class)
        private LocalDateTime startDt;
    
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "투두 스톱워치 종료 시간데이터", example = "2024-10-24 11:00:00", implementation = LocalDateTime.class)
        private LocalDateTime endDt;

    }

}
