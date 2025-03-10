package com.pape.timetodo.global.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "LOGGING")
@Data
public class LoggingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    @Comment(value = "로깅 IDX")
    private Long idx;

    @Column(name = "TYPE", nullable = false, length = 20)
    @Comment(value = "로깅 타입")
    private String type;

    @Column(name = "USERNAME", nullable = true, length = 500)
    @Comment(value = "회원아이디")
    private String username;

    @Column(name = "IP", nullable = false, length = 25)
    @Comment(value = "IP 주소")
    private String ip;

    @Column(name = "MESSAGE", nullable = true, length = 100)
    @Comment(value = "로그 메세지")
    private String message;

    @Column(name = "CREDENTIALS", nullable = true, length = 500)
    @Comment(value = "암호 및 중요정보")
    private String credentials;

    @Column(name = "CREATE_DT", nullable = true)
    @Comment(value = "생성일시")
    private LocalDateTime createDt = LocalDateTime.now();

}
