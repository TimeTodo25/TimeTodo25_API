package com.pape.timetodo.global.jpa.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pape.timetodo.global.jpa.entity.CategoryEntity;
import com.pape.timetodo.global.jpa.entity.QCategoryEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

        return query
            .selectFrom(qCategoryEntity)
            .where(builder)
            .fetchOne();
    }

    public List<CategoryEntity> findMyCategoryByUsresEntity(UsersEntity usersEntity){
        QCategoryEntity qCategoryEntity = QCategoryEntity.categoryEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCategoryEntity.deleteDt.isNull());

        return query
            .selectFrom(qCategoryEntity)
            .where(builder)
            .fetch();
    }
}
