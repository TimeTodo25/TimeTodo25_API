package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.jpa.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, String>{

    Optional<UsersEntity> findByEmail(String email);

} 
