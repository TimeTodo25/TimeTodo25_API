package com.pape.timetodo.global.jpa.repository;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.pape.timetodo.domain.main.model.DdayTodoModel;
import com.pape.timetodo.domain.main.model.GetRoutineModel;
import com.pape.timetodo.domain.main.model.GetTodoModel;
import com.pape.timetodo.global.jpa.entity.CategoryEntity;
import com.pape.timetodo.global.jpa.entity.QDdayEntity;
import com.pape.timetodo.global.jpa.entity.QRoutineEntity;
import com.pape.timetodo.global.jpa.entity.QTodoEntity;
import com.pape.timetodo.global.jpa.entity.QTodoTimerHistoryEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.pape.timetodo.global.util.DateUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateTimeTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.TimePath;
import com.querydsl.core.types.dsl.TimeTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TodoQueryRepository {

    private final JPAQueryFactory query;

    /**
     * TODO :: 리펙토링 필요 현재는 카테고리별로 회차하며 조회하나 모두 가져오는 것으로 조회해야할지도 모름
     * @param categoryEntity
     * @return
     */
    public List<GetTodoModel> findByCategoryAndDate(CategoryEntity categoryEntity, LocalDate date){

        QTodoEntity qTodoEntity = QTodoEntity.todoEntity;
        QRoutineEntity qRoutineEntity = QRoutineEntity.routineEntity;
        QTodoTimerHistoryEntity qTodoTimerHistoryEntity = QTodoTimerHistoryEntity.todoTimerHistoryEntity;

        BooleanExpression routineStratDt = Expressions.booleanTemplate("DATE_FORMAT({0}, '%Y-%m-%d') <= DATE_FORMAT({1}, '%Y-%m-%d')", qRoutineEntity.startDt, date.toString());
        BooleanExpression routineEndDt = Expressions.booleanTemplate("DATE_FORMAT({0}, '%Y-%m-%d') >= DATE_FORMAT({1}, '%Y-%m-%d')", qRoutineEntity.endDt, date.toString());

        // 루틴이 적용 되는지 확인
        BooleanBuilder routineCondition = new BooleanBuilder();
        routineCondition.and(qRoutineEntity.idx.isNotNull());
        routineCondition.and(routineStratDt);
        routineCondition.and(routineEndDt);

        BooleanExpression routineYn = Expressions
            .cases()
            .when(routineCondition)
                .then(true)
            .otherwise(false);

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTodoEntity.categoryEntity.eq(categoryEntity));
        builder.and(Expressions.booleanTemplate("DATE_FORMAT({0}, '%Y-%m-%d') = DATE_FORMAT({1}, '%Y-%m-%d')", qTodoEntity.targetDate, date.toString()));
        builder.or(routineYn.eq(true));
        builder.and(qTodoEntity.deleteDt.isNull());

        TimeTemplate<Time> todoTotalTm = Expressions.timeTemplate(Time.class,"SEC_TO_TIME(SUM(TIME_TO_SEC({0})))", qTodoTimerHistoryEntity.totalTm);

        return query
            .select(Projections.bean(
                GetTodoModel.class,
                qTodoEntity.idx.as("idx"),
                qTodoEntity.content.as("content"),
                qTodoEntity.targetDate.as("targetDate"),
                qTodoEntity.startTargetTm.as("startTargetTm"),
                qTodoEntity.endTargetTm.as("endTargetTm"),
                qTodoEntity.createDt.as("createDt"),
                routineYn.as("routineYn"),
                
                qRoutineEntity.cycleType.as("cycleType"),
                qRoutineEntity.cycleValue.as("cycleValue"),
                qRoutineEntity.rm.as("rm"),
                qRoutineEntity.startDt.as("routineStartDt"),
                qRoutineEntity.endDt.as("routineEndDt"),

                todoTotalTm.as("dummyTodoTotalTm")
            ))
            .from(qTodoEntity)
            .leftJoin(qRoutineEntity)
                .on(qTodoEntity.idx.eq(qRoutineEntity.todoEntity.idx))
            .leftJoin(qTodoTimerHistoryEntity)
                .on(qTodoEntity.idx.eq(qTodoTimerHistoryEntity.todoEntity.idx))
            .where(builder)
            .fetch();
    }

    public List<DdayTodoModel> findDdayTodoByUsersEntity(UsersEntity usersEntity){

        QDdayEntity qDdayEntity = QDdayEntity.ddayEntity;

        NumberTemplate<Integer> dDay = Expressions.numberTemplate(Integer.class, "TIMESTAMPDIFF(DAY, NOW(), {0})", qDdayEntity.targetDt);

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qDdayEntity.usersEntity.eq(usersEntity));

        return query
            .select(Projections.bean(
                DdayTodoModel.class,
                qDdayEntity.idx.as("idx"),
                qDdayEntity.content.as("content"),
                dDay.as("intervalDay"),
                qDdayEntity.createDt.as("createDt"),
                qDdayEntity.updateDt.as("updateDt")
            ))
            .from(qDdayEntity)
            .where(builder)
            .orderBy(dDay.asc())
            .fetch();
    }


}
