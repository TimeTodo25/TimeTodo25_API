package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTermsAgreeEntity is a Querydsl query type for TermsAgreeEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTermsAgreeEntity extends EntityPathBase<TermsAgreeEntity> {

    private static final long serialVersionUID = 1295704405L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTermsAgreeEntity termsAgreeEntity = new QTermsAgreeEntity("termsAgreeEntity");

    public final BooleanPath agreeYn = createBoolean("agreeYn");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deleteDt = createDateTime("deleteDt", java.time.LocalDateTime.class);

    public final NumberPath<Long> idx = createNumber("idx", Long.class);

    public final StringPath ip = createString("ip");

    public final ComparablePath<Character> status = createComparable("status", Character.class);

    public final QTermsEntity termsEntity;

    public final DateTimePath<java.time.LocalDateTime> updateDt = createDateTime("updateDt", java.time.LocalDateTime.class);

    public final QUsersEntity usersEntity;

    public QTermsAgreeEntity(String variable) {
        this(TermsAgreeEntity.class, forVariable(variable), INITS);
    }

    public QTermsAgreeEntity(Path<? extends TermsAgreeEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTermsAgreeEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTermsAgreeEntity(PathMetadata metadata, PathInits inits) {
        this(TermsAgreeEntity.class, metadata, inits);
    }

    public QTermsAgreeEntity(Class<? extends TermsAgreeEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.termsEntity = inits.isInitialized("termsEntity") ? new QTermsEntity(forProperty("termsEntity")) : null;
        this.usersEntity = inits.isInitialized("usersEntity") ? new QUsersEntity(forProperty("usersEntity")) : null;
    }

}

