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
@Table(name = "TODO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment(value = "TODO 테이블")
public class TodoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    @Comment(value = "IDX 고유값")
    private Long idx; // IDX 고유값

    @Column(name = "CONTENT", nullable = false)
    @Comment(value = "투두 내용")
    private String content; // 내용

    @Column(name = "TARGET_DATE", nullable = false)
    @Comment(value = "투두 지정일")
    private LocalDate targetDate; // 투두 지정일

    @Column(name = "START_TARGET_TM", nullable = true)
    @Comment(value = "투두 시작시간")
    private LocalTime startTargetTm; // 투두 시작시간

    @Column(name = "END_TARGET_TM", nullable = true)
    @Comment(value = "투두 종료시간")
    private LocalTime endTargetTm; // 투두 종료시간
    
    @Column(name = "CREATE_DT", nullable = false)
    @Comment(value = "생성일시")
    private LocalDateTime createDt; // 생성일시
    
    @Column(name = "UPDATE_DT", nullable = false)
    @Comment(value = "수정일시")
    private LocalDateTime updateDt; // 수정일시
    
    @Column(name = "DELETE_DT", nullable = true)
    @Comment(value = "삭제일시")
    private LocalDateTime deleteDt; // 삭제일시

    @Column(name = "STATUS", nullable = false)
    @Comment(value = "상태")
    private Character status; // 상태 - 삭제여부 등

    @Column(name = "PROGRESS_STATUS", nullable = false)
    @Comment(value = "진행도")
    private Integer progressStatus; // 진행도

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_IDX", referencedColumnName = "IDX")
    @Comment(value = "카테고리 IDX")
    private CategoryEntity categoryEntity; // 카테고리 Entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")
    @Comment(value = "작성자")
    private UsersEntity usersEntity; // 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROUTINE_IDX", referencedColumnName = "IDX")
    @Comment(value = "루틴 IDX")
    private RoutineEntity routineEntity;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "todoEntity")
    private List<TodoTimerHistoryEntity> todoTimerHistoryEntities;
    

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();
        this.status = StatusType.NORMAL.getValue();
        this.progressStatus = ProgressStatus._0.getValue();
    }

    @Getter
    @RequiredArgsConstructor
    public enum ProgressStatus {
        _100(100),
        _50(50),
        _0(0),
        ;

        private final Integer value;
    }
}
