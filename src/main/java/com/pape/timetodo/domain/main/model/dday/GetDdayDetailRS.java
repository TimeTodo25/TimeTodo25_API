package com.pape.timetodo.domain.main.model.dday;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetDdayDetailRS {

    private String content;

    private LocalDate ddayDate;

    private Boolean targetDelYn;
}
