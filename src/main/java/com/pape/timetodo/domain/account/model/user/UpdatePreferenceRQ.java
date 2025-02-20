package com.pape.timetodo.domain.account.model.user;

import com.pape.timetodo.global.constant.NotificationType;
import com.pape.timetodo.global.constant.SortType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Data
public class UpdatePreferenceRQ {

    @Schema(description = "D-Day 정렬 설정",
            example = """
            {
              "update": true,
              "values": ["D_REGISTRATION_ORDER", "D_COMPLETE_ORDER", "D_SWIPE_CHECK"]
            }
            """)
    private PreferenceUpdate<SortType> ddaySortType;

    @Schema(description = "카테고리 TODO 정렬 설정",
            example = """
            {
              "update": true,
              "values": ["C_REGISTRATION_ORDER", "C_COMPLETE_ORDER"]
            }
            """)
    private PreferenceUpdate<SortType> categorySortTypes;

    @Schema(description = "알림 설정",
            example = """
            {
              "update": true,
              "values": ["START_TODO_NOTI", "TARGET_DDAY_NOTI", "FRIEND_NEW_TODO_NOTI", "FRIEND_FOLLOW_NOTI"]
            }
            """)
    private PreferenceUpdate<NotificationType> notificationTypes;

    @Data
    public static class PreferenceUpdate<T> {

        private boolean update;
        private Set<T> values;

    }
}
