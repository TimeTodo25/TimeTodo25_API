package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.jpa.entity.HomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeRepository extends JpaRepository<HomeEntity, Long> {
}
