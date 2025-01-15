package com.pape.timetodo.global.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pape.timetodo.global.jpa.entity.MailEntity;

@Repository
public interface MailRepository extends JpaRepository<MailEntity, Long>{

}
