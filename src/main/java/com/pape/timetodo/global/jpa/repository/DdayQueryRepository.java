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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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

    @Transactional
    public List<DdayEntity> findAllToClose(LocalDate yesterday) {
        QDdayEntity qDdayEntity = QDdayEntity.ddayEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qDdayEntity.targetDt.eq(yesterday));
        builder.and(qDdayEntity.deleteDt.isNull());
        builder.and(qDdayEntity.status.notIn(StatusType.DELETED.getValue()));
        builder.and(qDdayEntity.targetDelYn.eq(true)); // 타겟날짜 지나면 삭제하는 데 동의

        return query
                .selectFrom(qDdayEntity)
                .where(builder)
                .fetch();
    }
}
