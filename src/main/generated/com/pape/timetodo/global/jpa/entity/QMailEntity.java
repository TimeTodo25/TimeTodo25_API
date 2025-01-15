package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMailEntity is a Querydsl query type for MailEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMailEntity extends EntityPathBase<MailEntity> {

    private static final long serialVersionUID = -1261940249L;

    public static final QMailEntity mailEntity = new QMailEntity("mailEntity");

    public final StringPath certNum = createString("certNum");

    public final BooleanPath certYn = createBoolean("certYn");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> idx = createNumber("idx", Long.class);

    public final EnumPath<MailEntity.MailType> mailType = createEnum("mailType", MailEntity.MailType.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updateDt = createDateTime("updateDt", java.time.LocalDateTime.class);

    public QMailEntity(String variable) {
        super(MailEntity.class, forVariable(variable));
    }

    public QMailEntity(Path<? extends MailEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMailEntity(PathMetadata metadata) {
        super(MailEntity.class, metadata);
    }

}

