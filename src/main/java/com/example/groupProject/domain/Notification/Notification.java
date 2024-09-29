package com.example.groupProject.domain.Notification;

import com.example.groupProject.domain.Memo.Memo;
import com.example.groupProject.domain.User.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private String title; //웹 푸시 제목

    private String content; //웹 푸시 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id")
    private User senderUser; // 알림을 보낸 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id")
    private User receiverUser; // 알림을 받는 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_memo_id")
    private Memo notificationMemo;

    private String url; // 알림 클릭 시 이동할 URL

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType; // 알림 타입

    @Enumerated(EnumType.STRING)
    private ReadStatus readStatus = ReadStatus.UNREAD; // 읽음 상태

    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

    private LocalDateTime readAt; // 읽은 시간 (선택적)

}

