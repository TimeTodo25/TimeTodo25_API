package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTodoEntity is a Querydsl query type for TodoEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTodoEntity extends EntityPathBase<TodoEntity> {

    private static final long serialVersionUID = 366300470L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTodoEntity todoEntity = new QTodoEntity("todoEntity");

    public final QCategoryEntity categoryEntity;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deleteDt = createDateTime("deleteDt", java.time.LocalDateTime.class);

    public final TimePath<java.time.LocalTime> endTargetTm = createTime("endTargetTm", java.time.LocalTime.class);

    public final NumberPath<Long> idx = createNumber("idx", Long.class);

    public final NumberPath<Integer> progressStatus = createNumber("progressStatus", Integer.class);

    public final QRoutineEntity routineEntity;

    public final TimePath<java.time.LocalTime> startTargetTm = createTime("startTargetTm", java.time.LocalTime.class);

    public final ComparablePath<Character> status = createComparable("status", Character.class);

    public final DatePath<java.time.LocalDate> targetDate = createDate("targetDate", java.time.LocalDate.class);

    public final ListPath<TodoTimerHistoryEntity, QTodoTimerHistoryEntity> todoTimerHistoryEntities = this.<TodoTimerHistoryEntity, QTodoTimerHistoryEntity>createList("todoTimerHistoryEntities", TodoTimerHistoryEntity.class, QTodoTimerHistoryEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> updateDt = createDateTime("updateDt", java.time.LocalDateTime.class);

    public final QUsersEntity usersEntity;

    public QTodoEntity(String variable) {
        this(TodoEntity.class, forVariable(variable), INITS);
    }

    public QTodoEntity(Path<? extends TodoEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTodoEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTodoEntity(PathMetadata metadata, PathInits inits) {
        this(TodoEntity.class, metadata, inits);
    }

    public QTodoEntity(Class<? extends TodoEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.categoryEntity = inits.isInitialized("categoryEntity") ? new QCategoryEntity(forProperty("categoryEntity"), inits.get("categoryEntity")) : null;
        this.routineEntity = inits.isInitialized("routineEntity") ? new QRoutineEntity(forProperty("routineEntity"), inits.get("routineEntity")) : null;
        this.usersEntity = inits.isInitialized("usersEntity") ? new QUsersEntity(forProperty("usersEntity")) : null;
    }

}

