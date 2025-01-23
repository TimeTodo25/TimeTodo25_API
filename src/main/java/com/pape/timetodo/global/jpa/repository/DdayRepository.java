package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.jpa.entity.DdayEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DdayRepository extends JpaRepository<DdayEntity, Long>{

    Optional<DdayEntity> findByIdxAndUsersEntity(Long idx, UsersEntity usersEntity);

    List<DdayEntity> findByUsersEntity(UsersEntity usersEntity);

}
