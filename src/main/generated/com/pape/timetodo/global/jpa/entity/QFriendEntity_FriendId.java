package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFriendEntity_FriendId is a Querydsl query type for FriendId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QFriendEntity_FriendId extends BeanPath<FriendEntity.FriendId> {

    private static final long serialVersionUID = -1702212583L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFriendEntity_FriendId friendId = new QFriendEntity_FriendId("friendId");

    public final QUsersEntity friendUsername;

    public final QUsersEntity username;

    public QFriendEntity_FriendId(String variable) {
        this(FriendEntity.FriendId.class, forVariable(variable), INITS);
    }

    public QFriendEntity_FriendId(Path<? extends FriendEntity.FriendId> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFriendEntity_FriendId(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFriendEntity_FriendId(PathMetadata metadata, PathInits inits) {
        this(FriendEntity.FriendId.class, metadata, inits);
    }

    public QFriendEntity_FriendId(Class<? extends FriendEntity.FriendId> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.friendUsername = inits.isInitialized("friendUsername") ? new QUsersEntity(forProperty("friendUsername"), inits.get("friendUsername")) : null;
        this.username = inits.isInitialized("username") ? new QUsersEntity(forProperty("username"), inits.get("username")) : null;
    }

}

