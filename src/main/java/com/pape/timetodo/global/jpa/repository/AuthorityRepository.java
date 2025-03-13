package com.pape.timetodo.global.jpa.repository;

import com.pape.timetodo.global.jpa.entity.AuthoritiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<AuthoritiesEntity, AuthoritiesEntity.AuthorityId> {
    void deleteAllByIdUsername(String username);
}
