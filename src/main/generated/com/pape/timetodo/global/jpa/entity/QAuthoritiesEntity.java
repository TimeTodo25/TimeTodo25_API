package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuthoritiesEntity is a Querydsl query type for AuthoritiesEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthoritiesEntity extends EntityPathBase<AuthoritiesEntity> {

    private static final long serialVersionUID = 511729751L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuthoritiesEntity authoritiesEntity = new QAuthoritiesEntity("authoritiesEntity");

    public final QAuthoritiesEntity_AuthorityId id;

    public QAuthoritiesEntity(String variable) {
        this(AuthoritiesEntity.class, forVariable(variable), INITS);
    }

    public QAuthoritiesEntity(Path<? extends AuthoritiesEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuthoritiesEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuthoritiesEntity(PathMetadata metadata, PathInits inits) {
        this(AuthoritiesEntity.class, metadata, inits);
    }

    public QAuthoritiesEntity(Class<? extends AuthoritiesEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new QAuthoritiesEntity_AuthorityId(forProperty("id")) : null;
    }

}

