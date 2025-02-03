package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHomeEntity is a Querydsl query type for HomeEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHomeEntity extends EntityPathBase<HomeEntity> {

    private static final long serialVersionUID = 944784143L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHomeEntity homeEntity = new QHomeEntity("homeEntity");

    public final StringPath goal = createString("goal");

    public final NumberPath<Long> idx = createNumber("idx", Long.class);

    public final EnumPath<com.pape.timetodo.global.constant.MoodType> mood = createEnum("mood", com.pape.timetodo.global.constant.MoodType.class);

    public final DatePath<java.time.LocalDate> todayDate = createDate("todayDate", java.time.LocalDate.class);

    public final QUsersEntity usersEntity;

    public QHomeEntity(String variable) {
        this(HomeEntity.class, forVariable(variable), INITS);
    }

    public QHomeEntity(Path<? extends HomeEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHomeEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHomeEntity(PathMetadata metadata, PathInits inits) {
        this(HomeEntity.class, metadata, inits);
    }

    public QHomeEntity(Class<? extends HomeEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.usersEntity = inits.isInitialized("usersEntity") ? new QUsersEntity(forProperty("usersEntity")) : null;
    }

}

