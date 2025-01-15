package com.pape.timetodo.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JpaQueryDSLConfig {

    private final EntityManager entityManager;

    @Bean
    public JPAQueryFactory entityManager(){
        return new JPAQueryFactory(this.entityManager);
    }
    
}
