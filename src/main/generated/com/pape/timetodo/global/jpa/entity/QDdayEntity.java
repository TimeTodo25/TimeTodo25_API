package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDdayEntity is a Querydsl query type for DdayEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDdayEntity extends EntityPathBase<DdayEntity> {

    private static final long serialVersionUID = 1269317672L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDdayEntity ddayEntity = new QDdayEntity("ddayEntity");

    public final BooleanPath completed = createBoolean("completed");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deleteDt = createDateTime("deleteDt", java.time.LocalDateTime.class);

    public final NumberPath<Long> idx = createNumber("idx", Long.class);

    public final ComparablePath<Character> status = createComparable("status", Character.class);

    public final BooleanPath targetDelYn = createBoolean("targetDelYn");

    public final DatePath<java.time.LocalDate> targetDt = createDate("targetDt", java.time.LocalDate.class);

    public final DateTimePath<java.time.LocalDateTime> updateDt = createDateTime("updateDt", java.time.LocalDateTime.class);

    public final QUsersEntity usersEntity;

    public QDdayEntity(String variable) {
        this(DdayEntity.class, forVariable(variable), INITS);
    }

    public QDdayEntity(Path<? extends DdayEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDdayEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDdayEntity(PathMetadata metadata, PathInits inits) {
        this(DdayEntity.class, metadata, inits);
    }

    public QDdayEntity(Class<? extends DdayEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.usersEntity = inits.isInitialized("usersEntity") ? new QUsersEntity(forProperty("usersEntity"), inits.get("usersEntity")) : null;
    }

}

