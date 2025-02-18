package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoutineEntity is a Querydsl query type for RoutineEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoutineEntity extends EntityPathBase<RoutineEntity> {

    private static final long serialVersionUID = 253466938L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRoutineEntity routineEntity = new QRoutineEntity("routineEntity");

    public final QCategoryEntity categoryEntity;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final EnumPath<RoutineEntity.CycleType> cycleType = createEnum("cycleType", RoutineEntity.CycleType.class);

    public final StringPath cycleValue = createString("cycleValue");

    public final DateTimePath<java.time.LocalDateTime> deleteDt = createDateTime("deleteDt", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> endDt = createDate("endDt", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> endTargetTm = createTime("endTargetTm", java.time.LocalTime.class);

    public final NumberPath<Long> idx = createNumber("idx", Long.class);

    public final StringPath rm = createString("rm");

    public final DatePath<java.time.LocalDate> startDt = createDate("startDt", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> startTargetTm = createTime("startTargetTm", java.time.LocalTime.class);

    public final ComparablePath<Character> status = createComparable("status", Character.class);

    public final ListPath<TodoEntity, QTodoEntity> todoEntities = this.<TodoEntity, QTodoEntity>createList("todoEntities", TodoEntity.class, QTodoEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> updateDt = createDateTime("updateDt", java.time.LocalDateTime.class);

    public final QUsersEntity usersEntity;

    public QRoutineEntity(String variable) {
        this(RoutineEntity.class, forVariable(variable), INITS);
    }

    public QRoutineEntity(Path<? extends RoutineEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRoutineEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRoutineEntity(PathMetadata metadata, PathInits inits) {
        this(RoutineEntity.class, metadata, inits);
    }

    public QRoutineEntity(Class<? extends RoutineEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.categoryEntity = inits.isInitialized("categoryEntity") ? new QCategoryEntity(forProperty("categoryEntity"), inits.get("categoryEntity")) : null;
        this.usersEntity = inits.isInitialized("usersEntity") ? new QUsersEntity(forProperty("usersEntity"), inits.get("usersEntity")) : null;
    }

}

