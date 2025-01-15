package com.pape.timetodo.global.jpa.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "ROUTINE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    @Comment(value = "루틴 IDX")
    private Long idx; // 인덱스

    @Enumerated(EnumType.STRING)
    @Column(name = "CYCLE_TYPE", nullable = false)
    @Comment(value = "루틴 반복타입")
    private CycleType cycleType; // 반복 타입

    @Column(name = "CYCLE_VALUE", nullable = false)
    @Comment(value = "루틴 반복값")
    private String cycleValue; // 반복 값

    @Column(name = "RM", nullable = true)
    @Comment(value = "비고, 설명")
    private String rm; // 비고란

    @Column(name = "START_DT", nullable = false)
    @Comment(value = "루틴 시작일")
    private LocalDate startDt; // 루틴 시작일

    @Column(name = "END_DT", nullable = false)
    @Comment(value = "루틴 종료일")
    private LocalDate endDt; // 루틴 종료일

    @Column(name = "CREATE_DT", nullable = false)
    @Comment(value = "생성일시")
    private LocalDateTime createDt;

    @Column(name = "UPDATE_DT", nullable = false)
    @Comment(value = "수정일시")
    private LocalDateTime updateDt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOOD_IDX", referencedColumnName = "IDX", nullable = false)
    @Comment(value = "루틴, 반복등록 할 투두 IDX")
    private TodoEntity todoEntity;

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
    public enum CycleType{
        EVERY_DAY,
        EVERY_WEEK,
        EVERY_MONTH
    }

}
