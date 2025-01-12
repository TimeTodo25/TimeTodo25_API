package com.pape.timetodo.global.jpa.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "CATEGORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    @Comment(value = "카테고리 IDX")
    private Long idx;

    @Column(name = "TITLE", nullable = false)
    @Comment(value = "카테고리 내용")
    private String title;

    @Column(name = "MAIN_COLOR", nullable = false)
    @Comment(value = "카테고리 색상")
    private String mainColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "PUBLIC_STATUS", nullable = false)
    @Comment(value = "카테고리 공개여부 [해당 카테고리 Todo 내용을 보일지 말지 결정]")
    private PublicStatus publicStatus;

    @Column(name = "CREATE_DT", nullable = false)
    @Comment(value = "생성일시")
    private LocalDateTime createDt;

    @Column(name = "UPDATE_DT", nullable = false)
    @Comment(value = "수정일시")
    private LocalDateTime updateDt;

    @Column(name = "DELETE_DT", nullable = true)
    @Comment(value = "삭제일시")
    private LocalDateTime deleteDt;

    @OneToMany(mappedBy = "categoryEntity")
    private List<TodoEntity> todoEntities;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME", nullable = false)
    @Comment(value = "작성자")
    private UsersEntity usersEntity;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();
    }

    @Getter
    @RequiredArgsConstructor
    public enum PublicStatus{
        PRIVATE("나만보기"),
        PUBLIC("전체공개"),
        PARTIAL("일부공개"),
        ;

        private final String desc;
    }
}
