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

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserPreferencesEntity userPreferencesEntity = new QUserPreferencesEntity("userPreferencesEntity");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final SetPath<com.pape.timetodo.global.constant.SortType, EnumPath<com.pape.timetodo.global.constant.SortType>> ddaySortType = this.<com.pape.timetodo.global.constant.SortType, EnumPath<com.pape.timetodo.global.constant.SortType>>createSet("ddaySortType", com.pape.timetodo.global.constant.SortType.class, EnumPath.class, PathInits.DIRECT2);

    public final SetPath<com.pape.timetodo.global.constant.NotificationType, EnumPath<com.pape.timetodo.global.constant.NotificationType>> notificationTypes = this.<com.pape.timetodo.global.constant.NotificationType, EnumPath<com.pape.timetodo.global.constant.NotificationType>>createSet("notificationTypes", com.pape.timetodo.global.constant.NotificationType.class, EnumPath.class, PathInits.DIRECT2);

    public final SetPath<com.pape.timetodo.global.constant.SortType, EnumPath<com.pape.timetodo.global.constant.SortType>> todoSortTypes = this.<com.pape.timetodo.global.constant.SortType, EnumPath<com.pape.timetodo.global.constant.SortType>>createSet("todoSortTypes", com.pape.timetodo.global.constant.SortType.class, EnumPath.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> updateDt = createDateTime("updateDt", java.time.LocalDateTime.class);

    public final QUsersEntity user;

    public final StringPath username = createString("username");

    public QUserPreferencesEntity(String variable) {
        this(UserPreferencesEntity.class, forVariable(variable), INITS);
    }

    public QUserPreferencesEntity(Path<? extends UserPreferencesEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserPreferencesEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserPreferencesEntity(PathMetadata metadata, PathInits inits) {
        this(UserPreferencesEntity.class, metadata, inits);
    }

    public QUserPreferencesEntity(Class<? extends UserPreferencesEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUsersEntity(forProperty("user"), inits.get("user")) : null;
    }

}

