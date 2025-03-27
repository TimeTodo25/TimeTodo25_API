package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.jpa.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pape.timetodo.global.jpa.entity.TodoTimerHistoryEntity;

@Repository
public interface TodoTimerHistoryRepository extends JpaRepository<TodoTimerHistoryEntity, Long>{
    boolean existsByTodoEntity(TodoEntity todo);
}
