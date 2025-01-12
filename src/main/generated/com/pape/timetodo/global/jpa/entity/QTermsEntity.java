package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTermsEntity is a Querydsl query type for TermsEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTermsEntity extends EntityPathBase<TermsEntity> {

    private static final long serialVersionUID = -1593187203L;

    public static final QTermsEntity termsEntity = new QTermsEntity("termsEntity");

    public final StringPath category = createString("category");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deleteDt = createDateTime("deleteDt", java.time.LocalDateTime.class);

    public final NumberPath<Long> idx = createNumber("idx", Long.class);

    public final EnumPath<TermsEntity.LangDivCd> LangDivCd = createEnum("LangDivCd", TermsEntity.LangDivCd.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updateDt = createDateTime("updateDt", java.time.LocalDateTime.class);

    public QTermsEntity(String variable) {
        super(TermsEntity.class, forVariable(variable));
    }

    public QTermsEntity(Path<? extends TermsEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTermsEntity(PathMetadata metadata) {
        super(TermsEntity.class, metadata);
    }

}

