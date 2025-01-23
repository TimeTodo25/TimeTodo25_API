package com.pape.timetodo.domain.main.model.home;

import com.pape.timetodo.global.constant.MoodType;
import lombok.Data;

import java.util.List;

@Data
public class GetHomeRS {

    private MoodType mood;

    private String goal;

    private List<HomeDdayModel> ddayList;

    private List<HomeCategoryModel> categoryList;

    private List<HomeTimerHistoryModel> timerHistoryList;

}
