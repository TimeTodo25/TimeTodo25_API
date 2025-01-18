package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.jpa.entity.RoutineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoutineRepository extends JpaRepository<RoutineEntity, Long>{

}
