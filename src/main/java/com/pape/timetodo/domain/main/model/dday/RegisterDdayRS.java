package com.pape.timetodo.domain.main.model.dday;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterDdayRS {

    private Long ddayIdx;

    private LocalDateTime updateDt;
}
