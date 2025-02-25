package com.pape.timetodo.domain.main.model.home;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HomeDdayModel {

    private Long ddayIdx;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetDt;

    private Boolean completed;

}
