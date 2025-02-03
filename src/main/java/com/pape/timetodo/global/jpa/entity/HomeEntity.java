package com.pape.timetodo.global.jpa.entity;


import com.pape.timetodo.global.constant.MoodType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Entity
@Table(name = "HOME")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment(value = "HOME 테이블")
public class HomeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    @Comment(value = "IDX 고유값")
    private Long idx; // IDX 고유값

    @Column(name = "TODAY_DATE", nullable = false)
    @Comment(value = "오늘 날짜")
    private LocalDate todayDate; // 오늘 날짜

    @Enumerated(EnumType.STRING)
    @Column(name = "MOOD", nullable = false)
    @Comment(value = "오늘의 기분")
    private MoodType mood; // 기분

    @Column(name = "GOAL", nullable = false)
    @Comment(value = "오늘의 목표")
    private String goal; // 목표

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")
    @Comment(value = "해당 유저")
    private UsersEntity usersEntity; // 해당 유저

    @PrePersist
    protected void onCreate() {
        this.todayDate = LocalDate.now();
    }
}
