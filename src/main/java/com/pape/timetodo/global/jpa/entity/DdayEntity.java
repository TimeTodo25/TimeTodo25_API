package com.pape.timetodo.global.jpa.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "D_DAY")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DdayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    @Comment(value = "PK고유키")
    private Long idx;

    @Column(name = "CONTENT", nullable = false)
    @Comment(value = "D_DAY 내용")
    private String content;

    @Column(name = "TARGET_DT", nullable = false)
    @Comment(value = "D_DAY 지정일")
    private LocalDate targetDt;

    @Column(name = "TARGET_DEL_YN", nullable = false)
    @Comment(value = "지정일 이후 삭제여부 [TRUE 일시 배치로 삭제 예정]")
    private Boolean targetDelYn;

    @Column(name = "CREATE_DT", nullable = false)
    @Comment(value = "생성일시")
    private LocalDateTime createDt;

    @Column(name = "UPDATE_DT", nullable = false)
    @Comment(value = "수정일시")
    private LocalDateTime updateDt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")
    @Comment(value = "등록유저")
    private UsersEntity usersEntity;


    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();
    }
}
