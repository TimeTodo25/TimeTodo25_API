package com.pape.timetodo.global.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pape.timetodo.global.jpa.entity.DdayEntity;

@Repository
public interface DdayRepository extends JpaRepository<DdayEntity, Long>{

}
