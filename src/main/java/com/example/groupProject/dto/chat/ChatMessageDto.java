package com.example.groupProject.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private enum MessageType {
        ENTER, TALK, QUIT, DELETE
    }

//    private MessageType type;   // 메시지 타입 (ENTER, TALK, QUIT, NEGO_REQ, NEGO_ALLOW, NEGO_DENIED, DELETE)
    private String roomId;      // 방 번호
//    private Long userId;        // 사용자 id
    private String message;     // 메시지
//    private String time;        // 전송 시간
//    private long userCount;     // 채팅방 인원 수
}
