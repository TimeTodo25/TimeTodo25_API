package com.pape.timetodo.global.jpa.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.pape.timetodo.global.jpa.entity.MailEntity;
import com.pape.timetodo.global.jpa.entity.QMailEntity;
import com.pape.timetodo.global.jpa.entity.MailEntity.MailType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MailQueryRepository {

    private final JPAQueryFactory query;

    public Optional<MailEntity> findByTop1EmailAndMailType(String email, MailType mailType){

        QMailEntity qMailEntity = QMailEntity.mailEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMailEntity.email.eq(email));
        builder.and(qMailEntity.mailType.eq(mailType));

        return Optional.ofNullable(
            query
                .select(qMailEntity)
                .from(qMailEntity)
                .where(builder)
                .orderBy(qMailEntity.createDt.desc())
                .limit(1)
                .fetchOne()
        );
    }

}
