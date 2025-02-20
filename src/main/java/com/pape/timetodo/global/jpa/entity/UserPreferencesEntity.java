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
    @Column(name = "USERNAME")
    private String username;

    @OneToOne
    @MapsId
    @JoinColumn(name = "USERNAME")
    private UsersEntity user;

    @Convert(converter = SortTypesConverter.class)
    @Column(name = "DDAY_SORT_TYPE")
    private Set<SortType> ddaySortType;

    @Convert(converter = SortTypesConverter.class)
    @Column(name = "CATEGORY_SORT_TYPES")
    private Set<SortType> categorySortTypes;

    @Convert(converter = NotificationTypesConverter.class)
    @Column(name = "NOTIFICATION_TYPES")
    private Set<NotificationType> notificationTypes;

    @Column(name = "CREATE_DT")
    private LocalDateTime createDt;

    @Column(name = "UPDATE_DT")
    private LocalDateTime updateDt;
}

