package com.pape.timetodo.domain.main.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pape.timetodo.domain.main.model.todo.RegistTodoTimerRQ;
import com.pape.timetodo.global.jpa.entity.CategoryEntity;
import com.pape.timetodo.global.jpa.entity.RoutineEntity;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LogoutSyncRQ {

    private List<CategoryDTO> categories;
    private List<RoutineDTO> routines;
    private List<TodoDTO> todos;
    private List<TodoTimerHistoryDTO> timerHistories;
    private List<DdayDTO> ddays;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CategoryDTO {

        @Schema(description = "카테고리 IDX - 있으면 수정, 없으면(null) 생성", example = "1", implementation = Long.class)
        private Long idx;

        @Schema(description = "카테고리 타이틀", example = "일", implementation = String.class)
        private String categoryTitle;

        @Schema(description = "공개타입[PRIVATE,PUBLIC,PARTIAL]", example = "PRIVATE", implementation = CategoryEntity.PublicStatus.class)
        private CategoryEntity.PublicStatus publicStatus;

        @Schema(description = "메인컬러", example = "#000000", implementation = String.class)
        private String mainColor;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RoutineDTO {

        @Schema(description = "루틴 IDX - 있으면 수정, 없으면(null) 생성", example = "1", implementation = Long.class)
        private Long routineIdx;

        @Schema(description = "기준 투두 IDX - 없으면(null) 새로 생성", example = "1", implementation = Long.class)
        private Long todoIdx;

        @NotNull
        @Schema(description = "투두/루틴 내용", example = "스웨거UI 문서화해주기", implementation = String.class)
        private String content;

        @NotNull
        @Schema(description = "카테고리 IDX", example = "1", implementation = Long.class)
        private Long categoryIdx;

        @NotNull
        @Schema(description = "반복타입[EVERY_DAY ,EVERY_WEEK, EVERY_MONTH]", example = "EVERY_WEEK", implementation = RoutineEntity.CycleType.class)
        private RoutineEntity.CycleType cycleType;

        @ArraySchema(schema = @Schema(description = "반복값[EVERY_DAY : value없음 , EVERY_WEEK: 1~7(1: 월요일, 2: 화요일 ... 7: 일요일), EVERY_MONTH : 1 ~ 31]", example = "1", implementation = Integer.class))
        private List<Byte> cycleValue;

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "루틴 시작일", example = "2024-11-01", implementation = LocalDate.class)
        private LocalDate startDt;

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "루틴 종료일", example = "2024-11-15", implementation = LocalDate.class)
        private LocalDate endDt;

        @NotNull
        @JsonFormat(pattern = "HH:mm:ss")
        @Schema(description = "투두 시작시간 [24시간 표시제]", example = "[HH:mm:ss] 09:00:00", implementation = LocalTime.class)
        private LocalTime startTargetTm;

        @NotNull
        @JsonFormat(pattern = "HH:mm:ss")
        @Schema(description = "투두 종료시간 [24시간 표시제]", example = "[HH:mm:ss] 11:00:00", implementation = LocalTime.class)
        private LocalTime endTargetTm;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TodoDTO {

        @Schema(description = "투두 IDX - 있으면 수정, 없으면(null) 생성", example = "1", implementation = Long.class)
        private Long idx;

        @NotNull
        @Schema(description = "투두 내용", example = "스웨거UI 문서화해주기", implementation = String.class)
        private String content;

        @NotNull
        @Schema(description = "카테고리 고유값", example = "1", implementation = Long.class)
        private Long categoryIdx;

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "투두 지정일", example = "[yyyy-MM-dd] 2024-10-30", implementation = LocalDate.class)
        private LocalDate date;

        @JsonFormat(pattern = "HH:mm:ss")
        @Schema(description = "투두 시작시간 [24시간 표시제]", example = "[HH:mm:ss] 09:00:00", implementation = LocalTime.class)
        private LocalTime startTargetTm;

        @JsonFormat(pattern = "HH:mm:ss")
        @Schema(description = "투두 종료시간 [24시간 표시제]", example = "[HH:mm:ss] 11:00:00", implementation = LocalTime.class)
        private LocalTime endTargetTm;

        @Schema(description = "진행도 - 0, 50, 100만 허용, 없으면 0", example = "0", implementation = Integer.class)
        private Integer progressStatus;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TodoTimerHistoryDTO {

        @NotNull
        @Schema(description = "투두 IDX", example = "1", implementation = Long.class)
        private Long todoIdx;

        @ArraySchema(schema = @Schema(implementation = RegistTodoTimerRQ.TimeData.class))
        private List<RegistTodoTimerRQ.TimeData> timeDatas;

        @Getter
        @Setter
        @NoArgsConstructor
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

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DdayDTO {

        @Schema(description = "디데이 IDX - 있으면 수정, 없으면(null) 생성", example = "1", implementation = Long.class)
        private Long idx;

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
}
