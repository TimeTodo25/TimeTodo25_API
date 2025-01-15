package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFriendEntity is a Querydsl query type for FriendEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFriendEntity extends EntityPathBase<FriendEntity> {

    private static final long serialVersionUID = -2053822898L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFriendEntity friendEntity = new QFriendEntity("friendEntity");

    public final BooleanPath acceptStatus = createBoolean("acceptStatus");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deleteDt = createDateTime("deleteDt", java.time.LocalDateTime.class);

    public final QFriendEntity_FriendId id;

    public final EnumPath<FriendEntity.RelationsShipType> relationsShipType = createEnum("relationsShipType", FriendEntity.RelationsShipType.class);

    public final EnumPath<FriendEntity.SendStatus> sendStatus = createEnum("sendStatus", FriendEntity.SendStatus.class);

    public final DateTimePath<java.time.LocalDateTime> updateDt = createDateTime("updateDt", java.time.LocalDateTime.class);

    public QFriendEntity(String variable) {
        this(FriendEntity.class, forVariable(variable), INITS);
    }

    public QFriendEntity(Path<? extends FriendEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFriendEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFriendEntity(PathMetadata metadata, PathInits inits) {
        this(FriendEntity.class, metadata, inits);
    }

    public QFriendEntity(Class<? extends FriendEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new QFriendEntity_FriendId(forProperty("id"), inits.get("id")) : null;
    }

}

