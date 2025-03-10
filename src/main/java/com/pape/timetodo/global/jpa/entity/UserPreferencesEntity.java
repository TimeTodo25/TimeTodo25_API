package com.pape.timetodo.global.jpa.entity;

import com.pape.timetodo.global.constant.NotificationType;
import com.pape.timetodo.global.constant.SortType;
import com.pape.timetodo.global.converter.NotificationTypesConverter;
import com.pape.timetodo.global.converter.SortTypesConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USER_PREFERENCES")
public class UserPreferencesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    @Comment(value = "사용자 설정 IDX")
    private Long preferenceId;

    @Convert(converter = SortTypesConverter.class)
    @Column(name = "DDAY_SORT_TYPE")
    @Comment(value = "D-day 정렬 타입")
    private Set<SortType> ddaySortType;

    @Convert(converter = SortTypesConverter.class)
    @Column(name = "CATEGORY_SORT_TYPES")
    @Comment(value = "카테고리 정렬 타입")
    private Set<SortType> categorySortTypes;

    @Convert(converter = NotificationTypesConverter.class)
    @Column(name = "NOTIFICATION_TYPES")
    @Comment(value = "알림 타입")
    private Set<NotificationType> notificationTypes;

    @Column(name = "OPTION_TERMS_AGREED")
    @Comment(value = "선택 약관 동의 여부")
    private boolean optionTermsAgreed;

    @Column(name = "CREATE_DT")
    @Comment(value = "생성일시")
    private LocalDateTime createDt;

    @Column(name = "UPDATE_DT")
    @Comment(value = "수정일시")
    private LocalDateTime updateDt;
}

