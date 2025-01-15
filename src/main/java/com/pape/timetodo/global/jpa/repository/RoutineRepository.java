package com.pape.timetodo.global.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pape.timetodo.global.jpa.entity.RoutineEntity;
import com.pape.timetodo.global.jpa.entity.TodoEntity;


@Repository
public interface RoutineRepository extends JpaRepository<RoutineEntity, Long>{

    Optional<RoutineEntity> findByTodoEntity(TodoEntity todoEntity);

}
