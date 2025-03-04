package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.constant.SortType;
import com.pape.timetodo.global.constant.StatusType;
import com.pape.timetodo.global.jpa.entity.DdayEntity;
import com.pape.timetodo.global.jpa.entity.QDdayEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * todoQueryRepository에도 같은 역할의 함수가 있는데 findDdayTodoByUsersEntity
     * 반환 모델이 다르고, 호출 순환 문제 때문에 일단은 그냥 둠. TODO: 리팩토링 시 고려할 것
     */
    public List<DdayEntity> findDdayByUsersEntity(UsersEntity usersEntity, LocalDate date, List<SortType> ddaySortTypeList) {

        QDdayEntity qDdayEntity = QDdayEntity.ddayEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qDdayEntity.usersEntity.eq(usersEntity));
        builder.and(qDdayEntity.status.notIn(StatusType.DELETED.getValue()));

        List<DdayEntity> results = query
                .select(Projections.bean(
                        DdayEntity.class,
                        qDdayEntity.idx.as("idx"),
                        qDdayEntity.content.as("content"),
                        qDdayEntity.targetDt.as("targetDt")
                ))
                .from(qDdayEntity)
                .where(builder)
                .fetch();

        // 정렬 기준 List
        List<Comparator<DdayEntity>> comparators = new ArrayList<>();
        // SortType 정렬 - 완료된 Dday는 뒤로 정렬
        if(ddaySortTypeList.contains(SortType.D_COMPLETE_ORDER)) {
            comparators.add(Comparator.comparing((DdayEntity d) -> d.isCompleted() ? 1 : 0));
        }
//        // SortType 정렬 - Dday 등록한 순으로 정렬
//        if(ddaySortTypeList.contains(SortType.D_REGISTRATION_ORDER))
//            comparators.add(Comparator.comparing(DdayEntity::getCreateDt));
        // 기본 정렬 - D-day가 아직 오지 않은 경우가 우선 정렬
        comparators.add(Comparator.comparing((DdayEntity d) -> d.getTargetDt().isBefore(date) ? 1 : 0));
        // 기본 정렬 - D-day가 가까울 수록 우선 정렬
        comparators.add(Comparator.comparingLong(d -> Math.abs(ChronoUnit.DAYS.between(date, d.getTargetDt()))));

        Comparator<DdayEntity> finalComparator = comparators.stream()
                .reduce(Comparator::thenComparing)
                .orElseThrow();

        return results.stream()
                .sorted(finalComparator)
                .collect(Collectors.toList());
    }
}
