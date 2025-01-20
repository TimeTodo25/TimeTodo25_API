package com.pape.timetodo.global.jpa.entity;

import com.pape.timetodo.global.constant.StatusType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "TERMS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TermsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    @Comment(value = "약관 IDX")
    private Long idx;

    @Column(name = "CATEGORY", nullable = false)
    @Comment(value = "약관 카테고리")
    private String category;

    @Column(name = "TITLE", nullable = false)
    @Comment(value = "약관 제목")
    private String title;
    
    @Column(name = "CONTENT", nullable = false)
    @Comment(value = "약관 내용")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "LANG_DIV_CD", nullable = false)
    @Comment(value = "약관 언어 타입 [KOR한국, ENG영어]")
    private LangDivCd LangDivCd;

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

    @Getter
    @RequiredArgsConstructor
    public enum LangDivCd{
        KOR("한국어"),
        ENG("영어"),
        ;

        private final String desc;
    }

}
