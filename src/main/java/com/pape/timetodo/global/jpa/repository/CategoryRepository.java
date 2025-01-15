package com.pape.timetodo.global.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pape.timetodo.global.jpa.entity.CategoryEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>{

    Optional<CategoryEntity> findByUsersEntityAndTitle(UsersEntity usersEntity, String title);

    List<CategoryEntity> findByUsersEntity(UsersEntity usersEntity);

    Optional<CategoryEntity> findByIdxAndUsersEntity(Long idx, UsersEntity usersEntity);

}
