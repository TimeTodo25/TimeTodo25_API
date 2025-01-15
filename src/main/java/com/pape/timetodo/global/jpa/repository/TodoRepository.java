package com.pape.timetodo.global.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pape.timetodo.global.jpa.entity.TodoEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;


@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long>{

    Optional<TodoEntity> findByIdxAndUsersEntity(Long idx, UsersEntity usersEntity);

}
