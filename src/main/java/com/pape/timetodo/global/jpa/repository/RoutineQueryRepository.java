package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.constant.StatusType;
import com.pape.timetodo.global.jpa.entity.QRoutineEntity;
import com.pape.timetodo.global.jpa.entity.RoutineEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RoutineQueryRepository {

    private final JPAQueryFactory query;

    public List<RoutineEntity> findMyRoutineByUsresEntity(UsersEntity usersEntity){
        QRoutineEntity qRoutineEntity = QRoutineEntity.routineEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qRoutineEntity.usersEntity.eq(usersEntity));
        builder.and(qRoutineEntity.deleteDt.isNull());
        builder.and(qRoutineEntity.status.notIn(StatusType.DELETED.getValue())); // 여러 상태 나열 가능

        return query
                .selectFrom(qRoutineEntity)
                .where(builder)
                .fetch();
    }
}
