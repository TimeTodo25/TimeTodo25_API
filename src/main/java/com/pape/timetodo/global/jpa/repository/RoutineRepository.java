package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.jpa.entity.RoutineEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoutineRepository extends JpaRepository<RoutineEntity, Long>{

    Optional<RoutineEntity> findByIdxAndUsersEntity(Long idx, UsersEntity usersEntity);

}
