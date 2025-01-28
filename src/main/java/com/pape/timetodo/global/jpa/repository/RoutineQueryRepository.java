package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.constant.StatusType;
import com.pape.timetodo.global.jpa.entity.CategoryEntity;
import com.pape.timetodo.global.jpa.entity.QRoutineEntity;
import com.pape.timetodo.global.jpa.entity.RoutineEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RoutineQueryRepository {

    private final JPAQueryFactory query;

    @PersistenceContext
    private EntityManager entityManager;

    public List<RoutineEntity> findMyRoutinesByUsresEntity(UsersEntity usersEntity){
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

    /**
     * 카테고리별 루틴 모두 조회하여 논리 삭제
     * @param categoryEntity CategoryEntity
     */
    @Transactional
    public void deleteRoutineListByCategory(CategoryEntity categoryEntity, LocalDateTime today){
        QRoutineEntity qRoutineEntity = QRoutineEntity.routineEntity;

        query.update(qRoutineEntity)
                .set(qRoutineEntity.deleteDt, today)
                .set(qRoutineEntity.status, StatusType.DELETED.getValue())
                .where(qRoutineEntity.categoryEntity.eq(categoryEntity)
                        .and(qRoutineEntity.status.notIn(StatusType.DELETED.getValue())))
                .execute();
    }

    /**
     * 카테고리별 오늘 이후까지 이어지는 루틴 모두 조회하여 카테고리 연결 끊기
     * @param categoryEntity CategoryEntity
     */
    @Transactional
    public void endWithCategoryRoutineListByCategory(CategoryEntity categoryEntity, LocalDateTime today){
        QRoutineEntity qRoutineEntity = QRoutineEntity.routineEntity;

        query.update(qRoutineEntity)
                .set(qRoutineEntity.categoryEntity, (CategoryEntity) null)
                .where(qRoutineEntity.categoryEntity.eq(categoryEntity)
                        .and(qRoutineEntity.status.notIn(StatusType.DELETED.getValue()))
                        .and(qRoutineEntity.endDt.after(LocalDate.from(today))))
                .execute();

        entityManager.flush(); // 변경 사항을 DB에 반영
        entityManager.clear(); // 1차 캐시 초기화
    }
}
