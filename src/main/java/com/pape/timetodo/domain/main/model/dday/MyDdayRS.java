package com.pape.timetodo.domain.main.model.dday;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MyDdayRS {

    private List<DdayModel> ddayList;

    @Data
    public static class DdayModel {

        private Long ddayIdx;

        private String content;

        private LocalDate ddayDate;

    }

}
