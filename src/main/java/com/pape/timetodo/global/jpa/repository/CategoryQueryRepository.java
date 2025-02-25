package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.constant.SortType;
import com.pape.timetodo.global.constant.StatusType;
import com.pape.timetodo.global.jpa.entity.CategoryEntity;
import com.pape.timetodo.global.jpa.entity.QCategoryEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CategoryQueryRepository {

    private final JPAQueryFactory query;

    public CategoryEntity findByIdAndUsersEntity(Long idx, UsersEntity usersEntity){
        QCategoryEntity qCategoryEntity = QCategoryEntity.categoryEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCategoryEntity.idx.eq(idx));
        builder.and(qCategoryEntity.usersEntity.eq(usersEntity));
        builder.and(qCategoryEntity.deleteDt.isNull());
        builder.and(qCategoryEntity.status.notIn(StatusType.DELETED.getValue()));

        return query
            .selectFrom(qCategoryEntity)
            .where(builder)
            .fetchOne();
    }

    public List<CategoryEntity> findMyCategoryByUsresEntity(UsersEntity usersEntity, List<SortType> categorySortTypeList){
        QCategoryEntity qCategoryEntity = QCategoryEntity.categoryEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCategoryEntity.usersEntity.eq(usersEntity));
        builder.and(qCategoryEntity.deleteDt.isNull());
        builder.and(qCategoryEntity.status.notIn(StatusType.DELETED.getValue())); // 여러 상태 나열 가능

        if(categorySortTypeList.contains(SortType.C_REGISTRATION_ORDER)) {
            return query
                    .selectFrom(qCategoryEntity)
                    .where(builder)
                    .orderBy(qCategoryEntity.createDt.asc())
                    .fetch();
        }

        return query
                .selectFrom(qCategoryEntity)
                .where(builder)
                .fetch();
    }
}
