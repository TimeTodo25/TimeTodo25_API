package com.pape.timetodo.global.jpa.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "TODO_TIMER_HISTORY")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoTimerHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    @Comment(value = "투두 시간 기록 IDX")
    private Long idx;

    @Column(name = "HISTORY_START_DT", nullable = false)
    @Comment(value = "투두 기록 시작 시간")
    private LocalDateTime historyStartDt;

    @Column(name = "HISTORY_END_DT", nullable = false)
    @Comment(value = "투두 기록 종료 시간")
    private LocalDateTime historyEndDt;

    @Column(name = "TOTAL_TM", nullable = false)
    @Comment(value = "투두 기록 걸린 총 시간")
    private LocalTime totalTm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TODO_IDX", referencedColumnName = "IDX", nullable = false)
    @Comment(value = "투두 IDX")
    private TodoEntity todoEntity;

}
