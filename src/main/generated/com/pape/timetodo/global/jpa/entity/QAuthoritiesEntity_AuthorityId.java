package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuthoritiesEntity_AuthorityId is a Querydsl query type for AuthorityId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QAuthoritiesEntity_AuthorityId extends BeanPath<AuthoritiesEntity.AuthorityId> {

    private static final long serialVersionUID = 2145140871L;

    public static final QAuthoritiesEntity_AuthorityId authorityId = new QAuthoritiesEntity_AuthorityId("authorityId");

    public final StringPath authority = createString("authority");

    public final StringPath username = createString("username");

    public QAuthoritiesEntity_AuthorityId(String variable) {
        super(AuthoritiesEntity.AuthorityId.class, forVariable(variable));
    }

    public QAuthoritiesEntity_AuthorityId(Path<? extends AuthoritiesEntity.AuthorityId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuthoritiesEntity_AuthorityId(PathMetadata metadata) {
        super(AuthoritiesEntity.AuthorityId.class, metadata);
    }

}

