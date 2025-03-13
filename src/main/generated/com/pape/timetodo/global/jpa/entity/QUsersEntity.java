package com.pape.timetodo.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUsersEntity is a Querydsl query type for UsersEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUsersEntity extends EntityPathBase<UsersEntity> {

    private static final long serialVersionUID = 299828286L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUsersEntity usersEntity = new QUsersEntity("usersEntity");

    public final BooleanPath accountNonExpired = createBoolean("accountNonExpired");

    public final BooleanPath accountNonLock = createBoolean("accountNonLock");

    public final SetPath<AuthoritiesEntity, QAuthoritiesEntity> authorities = this.<AuthoritiesEntity, QAuthoritiesEntity>createSet("authorities", AuthoritiesEntity.class, QAuthoritiesEntity.class, PathInits.DIRECT2);

    public final ListPath<CategoryEntity, QCategoryEntity> categoryEntities = this.<CategoryEntity, QCategoryEntity>createList("categoryEntities", CategoryEntity.class, QCategoryEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final ListPath<DdayEntity, QDdayEntity> ddayEntities = this.<DdayEntity, QDdayEntity>createList("ddayEntities", DdayEntity.class, QDdayEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> deleteDt = createDateTime("deleteDt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final BooleanPath enabled = createBoolean("enabled");

    public final ListPath<FriendEntity, QFriendEntity> friendEntities = this.<FriendEntity, QFriendEntity>createList("friendEntities", FriendEntity.class, QFriendEntity.class, PathInits.DIRECT2);

    public final ListPath<HomeEntity, QHomeEntity> homeEntities = this.<HomeEntity, QHomeEntity>createList("homeEntities", HomeEntity.class, QHomeEntity.class, PathInits.DIRECT2);

    public final StringPath nickname = createString("nickname");

    public final NumberPath<Integer> passFailCount = createNumber("passFailCount", Integer.class);

    public final StringPath password = createString("password");

    public final StringPath refreshToken = createString("refreshToken");

    public final ListPath<RoutineEntity, QRoutineEntity> routineEntity = this.<RoutineEntity, QRoutineEntity>createList("routineEntity", RoutineEntity.class, QRoutineEntity.class, PathInits.DIRECT2);

    public final ComparablePath<Character> status = createComparable("status", Character.class);

    public final ListPath<TodoEntity, QTodoEntity> todoEntities = this.<TodoEntity, QTodoEntity>createList("todoEntities", TodoEntity.class, QTodoEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> updateDt = createDateTime("updateDt", java.time.LocalDateTime.class);

    public final StringPath username = createString("username");

    public final QUserPreferencesEntity userPreferences;

    public QUsersEntity(String variable) {
        this(UsersEntity.class, forVariable(variable), INITS);
    }

    public QUsersEntity(Path<? extends UsersEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUsersEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUsersEntity(PathMetadata metadata, PathInits inits) {
        this(UsersEntity.class, metadata, inits);
    }

    public QUsersEntity(Class<? extends UsersEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userPreferences = inits.isInitialized("userPreferences") ? new QUserPreferencesEntity(forProperty("userPreferences")) : null;
    }

}

