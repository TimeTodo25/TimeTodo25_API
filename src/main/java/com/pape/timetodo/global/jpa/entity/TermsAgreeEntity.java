package com.pape.timetodo.global.jpa.entity;

import com.pape.timetodo.global.constant.StatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "TERMS_AGREE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermsAgreeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    @Comment(value = "약관 동의 IDX")
    private Long idx;

    @Column(name = "IP", nullable = false)
    @Comment(value = "약관 동의 한 IP주소")
    private String ip;

    @Column(name = "AGREE_YN", nullable = false)
    @Comment(value = "동의여부")
    private Boolean agreeYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")
    @Comment(value = "약관 동의자 아이디")
    private UsersEntity usersEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TERMS_IDX", referencedColumnName = "IDX")
    @Comment(value = "약관 IDX")
    private TermsEntity termsEntity;

    @Column(name = "CREATE_DT", nullable = false)
    @Comment(value = "생성일시")
    private LocalDateTime createDt;

    @Column(name = "UPDATE_DT", nullable = false)
    @Comment(value = "수정일시")
    private LocalDateTime updateDt;

    @Column(name = "DELETE_DT", nullable = true)
    @Comment(value = "삭제일시")
    private LocalDateTime deleteDt;

    @Column(name = "STATUS", nullable = false)
    @Comment(value = "상태")
    private Character status;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();
        this.status = StatusType.NORMAL.getValue();
    }
}
