package com.pape.timetodo.global.jpa.entity;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USERS")
public class UsersEntity implements UserDetails{

    @Id
    @Column(name = "USERNAME", nullable = false)
    @Comment(value = "아이디")
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    @Comment(value = "암호")
    private String password;    

    @Column(name = "EMAIL", nullable = false)
    @Comment(value = "이메일")
    private String email;

    @Column(name = "NICKNAME", nullable = false)
    @Comment(value = "닉네임")
    private String nickname;

    @Column(name = "ENABLED", nullable = false)
    @Comment(value = "활성화 여부")
    private Boolean enabled;

    @Column(name = "ACCOUNT_NON_LOCK", nullable = false)
    @Comment(value = "계정 잠금 여부")
    private Boolean accountNonLock;

    @Column(name = "ACCOUNT_NON_EXPIRED", nullable = false)
    @Comment(value = "계정 만료 여부")
    private Boolean accountNonExpired;

    @Column(name = "PASS_FAIL_COUNT", nullable = false)
    @Comment(value = "계정 틀린횟수")
    private Integer passFailCount;

    @Column(name = "REFRESH_TOKEN", nullable = true)
    @Comment(value = "리프레쉬 토큰")
    private String refreshToken;
    
    @Column(name = "CREATE_DT", nullable = false)
    @Comment(value = "생성일시")
    private LocalDateTime createDt;
    
    @Column(name = "UPDATE_DT", nullable = false)
    @Comment(value = "수정일시")
    private LocalDateTime updateDt;

    @Column(name = "DELETE_DT", nullable = true)
    @Comment(value = "삭제일시")
    private LocalDateTime deleteDt;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")
    private Set<AuthoritiesEntity> authorities;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "usersEntity")
    private List<TodoEntity> todoEntities;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "usersEntity")
    private List<CategoryEntity> categoryEntities;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.username")
    private List<FriendEntity> friendEntities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(AuthoritiesEntity authoritiesEntity) {

        if(authorities == null){
            this.authorities = new HashSet<>();
        }

        this.authorities.add(authoritiesEntity);
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLock;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();
    }
   
}
