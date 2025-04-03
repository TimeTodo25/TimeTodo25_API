package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.jpa.entity.RoutineEntity;
import com.pape.timetodo.global.jpa.entity.TodoEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long>{

    Optional<TodoEntity> findByIdxAndUsersEntity(Long idx, UsersEntity usersEntity);

    List<TodoEntity> findByRoutineEntityAndProgressStatus(RoutineEntity entity, int progressStatus);

    List<TodoEntity> findByUsersEntityAndStatus(UsersEntity usersEntity, Character statusType);

    List<TodoEntity> findByRoutineEntity(RoutineEntity routine);
}
