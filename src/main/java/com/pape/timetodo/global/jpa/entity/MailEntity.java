package com.pape.timetodo.global.jpa.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@Table(name = "MAIL")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    @Comment(value = "메일 IDX 번호")
    private Long idx;

    @Column(name = "EMAIL", nullable = false)
    @Comment(value = "이메일")
    private String email;

    @Column(name = "MAIL_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment(value = "메일타입 [회원가입 메일, 암호 수정 인증메일 등]")
    private MailType mailType;

    @Column(name = "TITLE", nullable = false)
    @Comment(value = "메일 제목")
    private String title;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "LONGTEXT")
    @Comment(value = "메일 내용")
    private String content;

    @Column(name = "CERT_NUM", nullable = true)
    @Comment(value = "인증번호 [인증메일이 아니면 없을 수 있음]")
    private String certNum;

    @Column(name = "CERT_YN", nullable = true)
    @Comment(value = "인증여부")
    private Boolean certYn;

    @Column(name = "CREATE_DT", nullable = false)
    @Comment(value = "생성일시")
    private LocalDateTime createDt;

    @Column(name = "UPDATE_DT", nullable = false)
    @Comment(value = "수정일시")
    private LocalDateTime updateDt;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();
    }


    @RequiredArgsConstructor
    @Getter
    public static enum MailType{
        REGISTER_CERT("회원가입시 인증메일"),
        UPDATE_CERT("정보 업데이트 인증메일[아이디 찾기, 암호변경 등..]"),
        GUIDE("안내"),
        ;

        private final String desc;
    }

}
