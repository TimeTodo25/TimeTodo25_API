package com.pape.timetodo.global.jpa.entity;

import java.io.Serializable;

import org.hibernate.annotations.Comment;
import org.springframework.security.core.GrantedAuthority;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "AUTHORITIES")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthoritiesEntity implements GrantedAuthority{

    @EmbeddedId
    private AuthorityId id;

    @Override
    public String getAuthority() {
        return id.authority;
    }

    @Data
    @Embeddable
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorityId implements Serializable {

        @Column(name = "USERNAME", nullable = false)
        @Comment(value = "유저 아이디")
        private String username;

        @Column(name = "AUTHORITY", nullable = false)
        @Comment(value = "권한")
        private String authority;

        // constructors, equals, hashCode, getters, setters 생략
    }

}
