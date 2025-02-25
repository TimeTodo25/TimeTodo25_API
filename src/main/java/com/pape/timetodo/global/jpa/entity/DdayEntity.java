package com.pape.timetodo.global.jpa.entity;

import com.pape.timetodo.global.constant.StatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "DELETE_DT", nullable = true)
    @Comment(value = "삭제일시")
    private LocalDateTime deleteDt; // 삭제일시

    @Column(name = "STATUS", nullable = false)
    @Comment(value = "상태")
    private Character status; // 상태 - 삭제여부 등

    @Column(name = "COMPLETED", nullable = false)
    @Comment(value = "완료 여부")
    private Boolean completed; // 완료 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")
    @Comment(value = "등록유저")
    private UsersEntity usersEntity;


    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();
        this.status = StatusType.NORMAL.getValue();
        this.completed = false;
    }
}
