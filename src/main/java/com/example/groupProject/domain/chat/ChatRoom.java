package com.example.groupProject.domain.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoom {
    private static final int USER_PER_COUNT = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomLeaderId; //채팅방 방장
    private long userCount; //채팅방 인원수

    public void enterRoom() {
        this.userCount += USER_PER_COUNT;
    }

    public void quitRoom() {
        this.userCount -= USER_PER_COUNT;
    }
}
