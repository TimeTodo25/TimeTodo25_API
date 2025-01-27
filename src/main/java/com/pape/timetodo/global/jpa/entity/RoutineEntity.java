package com.pape.timetodo.global.jpa.entity;

import com.pape.timetodo.global.constant.StatusType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    @Column(name = "CONTENT", nullable = false)
    @Comment(value = "루틴 내용")
    private String content; // 내용

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

    @Column(name = "START_TARGET_TM", nullable = true)
    @Comment(value = "루틴의 투두 시작시간")
    private LocalTime startTargetTm; // 루틴의 투두 시작시간

    @Column(name = "END_TARGET_TM", nullable = true)
    @Comment(value = "루틴의 투두 종료시간")
    private LocalTime endTargetTm; // 루틴의 투두 종료시간

    @Column(name = "CREATE_DT", nullable = false)
    @Comment(value = "생성일시")
    private LocalDateTime createDt;

    @Column(name = "UPDATE_DT", nullable = false)
    @Comment(value = "수정일시")
    private LocalDateTime updateDt;

    @Column(name = "DELETE_DT", nullable = true)
    @Comment(value = "삭제일시")
    private LocalDateTime deleteDt; // 삭제일시

    @Column(name = "STATUS", nullable = false)
    @Comment(value = "상태")
    private Character status; // 상태 - 삭제여부 등

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "routineEntity")
    private List<TodoEntity> todoEntities;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME", nullable = false)
    @Comment(value = "작성자")
    private UsersEntity usersEntity;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();
        this.status = StatusType.NORMAL.getValue();
    }

    @Getter
    @RequiredArgsConstructor
    public enum CycleType{
        EVERY_DAY,
        EVERY_WEEK,
        EVERY_MONTH
    }

}
