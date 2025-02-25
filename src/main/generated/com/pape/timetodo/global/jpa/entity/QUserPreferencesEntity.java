package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserPreferencesEntity is a Querydsl query type for UserPreferencesEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPreferencesEntity extends EntityPathBase<UserPreferencesEntity> {

    private static final long serialVersionUID = 1675526371L;

    public static final QUserPreferencesEntity userPreferencesEntity = new QUserPreferencesEntity("userPreferencesEntity");

    public final SetPath<com.pape.timetodo.global.constant.SortType, EnumPath<com.pape.timetodo.global.constant.SortType>> categorySortTypes = this.<com.pape.timetodo.global.constant.SortType, EnumPath<com.pape.timetodo.global.constant.SortType>>createSet("categorySortTypes", com.pape.timetodo.global.constant.SortType.class, EnumPath.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final SetPath<com.pape.timetodo.global.constant.SortType, EnumPath<com.pape.timetodo.global.constant.SortType>> ddaySortType = this.<com.pape.timetodo.global.constant.SortType, EnumPath<com.pape.timetodo.global.constant.SortType>>createSet("ddaySortType", com.pape.timetodo.global.constant.SortType.class, EnumPath.class, PathInits.DIRECT2);

    public final SetPath<com.pape.timetodo.global.constant.NotificationType, EnumPath<com.pape.timetodo.global.constant.NotificationType>> notificationTypes = this.<com.pape.timetodo.global.constant.NotificationType, EnumPath<com.pape.timetodo.global.constant.NotificationType>>createSet("notificationTypes", com.pape.timetodo.global.constant.NotificationType.class, EnumPath.class, PathInits.DIRECT2);

    public final NumberPath<Long> preferenceId = createNumber("preferenceId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updateDt = createDateTime("updateDt", java.time.LocalDateTime.class);

    public QUserPreferencesEntity(String variable) {
        super(UserPreferencesEntity.class, forVariable(variable));
    }

    public QUserPreferencesEntity(Path<? extends UserPreferencesEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserPreferencesEntity(PathMetadata metadata) {
        super(UserPreferencesEntity.class, metadata);
    }

}

