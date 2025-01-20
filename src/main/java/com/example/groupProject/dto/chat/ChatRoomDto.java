package com.example.groupProject.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto {

    private String roomName;
    private String roomLeaderName;
    private long userCount;

    public static ChatRoomDto of(String roomName, String roomLeaderName, long userCount) {
        return ChatRoomDto.builder()
                .roomName(roomName)
                .roomLeaderName(roomLeaderName)
                .userCount(userCount)
                .build();
    }
}
