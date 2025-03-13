package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLoggingEntity is a Querydsl query type for LoggingEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLoggingEntity extends EntityPathBase<LoggingEntity> {

    private static final long serialVersionUID = 1436115861L;

    public static final QLoggingEntity loggingEntity = new QLoggingEntity("loggingEntity");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final StringPath credentials = createString("credentials");

    public final NumberPath<Long> idx = createNumber("idx", Long.class);

    public final StringPath ip = createString("ip");

    public final StringPath message = createString("message");

    public final StringPath type = createString("type");

    public final StringPath username = createString("username");

    public QLoggingEntity(String variable) {
        super(LoggingEntity.class, forVariable(variable));
    }

    public QLoggingEntity(Path<? extends LoggingEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLoggingEntity(PathMetadata metadata) {
        super(LoggingEntity.class, metadata);
    }

}

