package com.pape.timetodo.global.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pape.timetodo.global.jpa.entity.TodoTimerHistoryEntity;

@Repository
public interface TodoTimerHistoryRepository extends JpaRepository<TodoTimerHistoryEntity, Long>{

}
