package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.domain.main.model.home.HomeDdayModel;
import com.pape.timetodo.domain.main.model.home.HomeTodoModel;
import com.pape.timetodo.global.constant.StatusType;
import com.pape.timetodo.global.jpa.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.TimeTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TodoQueryRepository {

    private final JPAQueryFactory query;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 카테고리별로 회차하며 해당 날짜의 투두 모두 조회
     * @param categoryEntity CategoryEntity
     * @param date LocalDate
     * @return HomeTodoModel
     */
    public List<HomeTodoModel> findByCategoryAndDate(CategoryEntity categoryEntity, LocalDate date){

        QTodoEntity qTodoEntity = QTodoEntity.todoEntity;
        QTodoTimerHistoryEntity qTodoTimerHistoryEntity = QTodoTimerHistoryEntity.todoTimerHistoryEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTodoEntity.categoryEntity.eq(categoryEntity));
        builder.and(Expressions.booleanTemplate("DATE_FORMAT({0}, '%Y-%m-%d') = DATE_FORMAT({1}, '%Y-%m-%d')", qTodoEntity.targetDate, date.toString()));
        builder.and(qTodoEntity.status.notIn(StatusType.DELETED.getValue()));

        TimeTemplate<Time> todoTotalTm = Expressions.timeTemplate(Time.class,"SEC_TO_TIME(SUM(TIME_TO_SEC({0})))", qTodoTimerHistoryEntity.totalTm);

        return query
            .select(Projections.bean(
                    HomeTodoModel.class,
                qTodoEntity.idx.as("idx"),
                qTodoEntity.content.as("content"),
                qTodoEntity.targetDate.as("targetDate"),
                qTodoEntity.startTargetTm.as("startTargetTm"),
                qTodoEntity.endTargetTm.as("endTargetTm"),
                qTodoEntity.createDt.as("createDt"),

                todoTotalTm.as("dummyTodoTotalTm")
            ))
            .from(qTodoEntity)
            .leftJoin(qTodoTimerHistoryEntity)
                .on(qTodoEntity.idx.eq(qTodoTimerHistoryEntity.todoEntity.idx))
            .where(builder)
            .fetch();
    }

    /**
    * 디데이 -/+ 계산은 앱단에서 하기로 결정함에 따라,
    * 기존의 남은 날짜 반환하던 로직을 디데이 해당 날짜 반환하도록 수정함
     */
    public List<HomeDdayModel> findDdayTodoByUsersEntity(UsersEntity usersEntity, LocalDate date) {

        QDdayEntity qDdayEntity = QDdayEntity.ddayEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qDdayEntity.usersEntity.eq(usersEntity));
        builder.and(
                qDdayEntity.targetDelYn.isFalse() // ddayDelYn이 false인 경우 = 해당 날짜 도래해도 삭제하지 않음 옵션의 디데이만 포함
                        .or(qDdayEntity.targetDt.goe(date)) // 또는 targetDt(디데이 날짜)가 date(기준 날짜)보다 미래이거나 같은 경우 포함
        );
        // TODO: 차후 status 적용해서 그냥 삭제되지 않은 Dday 모두 가져가도록 할 예정 (자동 삭제 적용 이후)

        return query
                .select(Projections.bean(
                        HomeDdayModel.class,
                        qDdayEntity.idx.as("idx"),
                        qDdayEntity.content.as("content"),
                        qDdayEntity.targetDt.as("targetDt")
                ))
                .from(qDdayEntity)
                .where(builder)
                .orderBy(qDdayEntity.targetDt.asc())
                .fetch();
    }


    /**
     * 삭제되지 않았고 개별 수정되지 않은 투두 목록 조회
     * @param routineEntity RoutineEntity
     * @return List<TodoEntity>
     */
    public List<TodoEntity> findTodoListByRoutine(RoutineEntity routineEntity) {
        QTodoEntity qTodoEntity = QTodoEntity.todoEntity;

        LocalDate today = LocalDate.now();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTodoEntity.routineEntity.eq(routineEntity)); // 해당 루틴에 속한 투두
        builder.and(qTodoEntity.status.notIn(StatusType.DELETED.getValue(), StatusType.UPDATED.getValue())); // 상태가 D나 U가 아닌 것
        builder.and(qTodoEntity.targetDate.goe(today)); // 타겟 날짜가 오늘 이후인 것 (오늘 포함)

        return query
                .selectFrom(qTodoEntity)
                .where(builder)
                .orderBy(qTodoEntity.targetDate.asc()) // 타겟 날짜 기준으로 오름차순 정렬
                .fetch();

    }

    /**
     * 카테고리별 투두 모두 조회하여 논리 삭제
     * @param categoryEntity CategoryEntity
     */
    @Transactional
    public void deleteTodoListByCategory(CategoryEntity categoryEntity, LocalDateTime today){
        QTodoEntity qTodoEntity = QTodoEntity.todoEntity;

        query.update(qTodoEntity)
                .set(qTodoEntity.deleteDt, today)
                .set(qTodoEntity.status, StatusType.DELETED.getValue())
                .where(qTodoEntity.categoryEntity.eq(categoryEntity)
                        .and(qTodoEntity.status.notIn(StatusType.DELETED.getValue())))
                .execute();
    }


    /**
     * 카테고리별 오늘 이후 해당하는 투두 모두 조회하여 카테고리 연결 끊기
     * @param categoryEntity CategoryEntity
     */
    @Transactional
    public void endWithCategoryTodoListByCategory(CategoryEntity categoryEntity, LocalDateTime today){
        QTodoEntity qTodoEntity = QTodoEntity.todoEntity;

        query.update(qTodoEntity)
                .set(qTodoEntity.categoryEntity, (CategoryEntity) null)
                .where(qTodoEntity.categoryEntity.eq(categoryEntity)
                        .and(qTodoEntity.status.notIn(StatusType.DELETED.getValue()))
                        .and(qTodoEntity.targetDate.after(LocalDate.from(today))))
                .execute();

        entityManager.flush(); // 변경 사항을 DB에 반영
        entityManager.clear(); // 1차 캐시 초기화
    }
}
