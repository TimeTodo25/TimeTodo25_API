package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTodoTimerHistoryEntity is a Querydsl query type for TodoTimerHistoryEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTodoTimerHistoryEntity extends EntityPathBase<TodoTimerHistoryEntity> {

    private static final long serialVersionUID = 1872026405L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTodoTimerHistoryEntity todoTimerHistoryEntity = new QTodoTimerHistoryEntity("todoTimerHistoryEntity");

    public final DateTimePath<java.time.LocalDateTime> historyEndDt = createDateTime("historyEndDt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> historyStartDt = createDateTime("historyStartDt", java.time.LocalDateTime.class);

    public final NumberPath<Long> idx = createNumber("idx", Long.class);

    public final QTodoEntity todoEntity;

    public final TimePath<java.time.LocalTime> totalTm = createTime("totalTm", java.time.LocalTime.class);

    public QTodoTimerHistoryEntity(String variable) {
        this(TodoTimerHistoryEntity.class, forVariable(variable), INITS);
    }

    public QTodoTimerHistoryEntity(Path<? extends TodoTimerHistoryEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTodoTimerHistoryEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTodoTimerHistoryEntity(PathMetadata metadata, PathInits inits) {
        this(TodoTimerHistoryEntity.class, metadata, inits);
    }

    public QTodoTimerHistoryEntity(Class<? extends TodoTimerHistoryEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.todoEntity = inits.isInitialized("todoEntity") ? new QTodoEntity(forProperty("todoEntity"), inits.get("todoEntity")) : null;
    }

}

