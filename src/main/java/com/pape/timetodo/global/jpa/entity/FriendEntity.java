package com.pape.timetodo.global.jpa.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "FRIEND")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendEntity {

    @EmbeddedId
    private FriendId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "RELATIONS_SHIP_TYPE", nullable = false)
    @Comment(value = """
        [친구타입]
        PARENT : 멘토
        CHILD : 멘티
        FRIEND : 친구
        BUSINESS : 비지니스
    """)
    private RelationsShipType relationsShipType;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEND_STATUS", nullable = false)
    @Comment(value = "친구요청 발신 상태 [발신, 수신]")
    private SendStatus sendStatus;

    @Column(name = "ACCEPT_STATUS", nullable = false)
    @Comment(value = "친구요청 수락 상태 True : 친구, False : 친구 아님")
    private Boolean acceptStatus;

    @Column(name = "CREATE_DT", nullable = false)
    @Comment(value = "생성일시")
    private LocalDateTime createDt;
    
    @Column(name = "UPDATE_DT", nullable = false)
    @Comment(value = "수정일시")
    private LocalDateTime updateDt;

    @Column(name = "DELETE_DT", nullable = true)
    @Comment(value = "삭제일시")
    private LocalDateTime deleteDt;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();
    }


    @Getter
    @Setter
    @Embeddable
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendId implements Serializable {

        private static final long serialVersionUID = 28111435523722056L;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME", nullable = false)
        private UsersEntity username;
    
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "FRIEND_USERNAME", referencedColumnName = "USERNAME", nullable = false)
        private UsersEntity friendUsername;

        @Override
        public boolean equals(Object o){
            if(this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;
            FriendId that = (FriendId) o;
            return Objects.equals(username, that.username) && Objects.equals(friendUsername, that.friendUsername);
        }

        @Override
        public int hashCode(){
            return Objects.hash(username, friendUsername);
        }
        

    }


    @Getter
    @RequiredArgsConstructor
    public enum RelationsShipType {
        PARENT("멘토","데이터 관여가능"),
        CHILD("멘티","데이터 관여불가"),
        FRIEND("친구","서로 데이터 관여불가"),
        BUSINESS("비지니스","서로 데이터 관여가능"),
        ;

        private final String grade;
        private final String desc;
    }

    @Getter
    @RequiredArgsConstructor
    public enum SendStatus{
        SEND("발신"),
        RECEIVE("수신"),
        ;

        private final String desc;
    }
}
