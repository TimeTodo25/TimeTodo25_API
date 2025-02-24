package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.jpa.entity.CategoryEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>{

    Optional<CategoryEntity> findByUsersEntityAndTitle(UsersEntity usersEntity, String title);

    List<CategoryEntity> findByUsersEntity(UsersEntity usersEntity);

    List<CategoryEntity> findByUsersEntityOrderByCreateDtAsc(UsersEntity usersEntity);

    Optional<CategoryEntity> findByIdxAndUsersEntity(Long idx, UsersEntity usersEntity);

}
