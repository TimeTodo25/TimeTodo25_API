package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.constant.StatusType;
import com.pape.timetodo.global.jpa.entity.DdayEntity;
import com.pape.timetodo.global.jpa.entity.QDdayEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DdayQueryRepository {

    private final JPAQueryFactory query;

    public DdayEntity findByIdAndUsersEntity(Long idx, UsersEntity usersEntity){
        QDdayEntity qDdayEntity = QDdayEntity.ddayEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qDdayEntity.idx.eq(idx));
        builder.and(qDdayEntity.usersEntity.eq(usersEntity));
        builder.and(qDdayEntity.deleteDt.isNull());
        builder.and(qDdayEntity.status.notIn(StatusType.DELETED.getValue()));

        return query
                .selectFrom(qDdayEntity)
                .where(builder)
                .fetchOne();
    }
}
