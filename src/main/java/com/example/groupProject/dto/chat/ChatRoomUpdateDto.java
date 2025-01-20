package com.example.groupProject.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomUpdateDto {

    private Long roomId;
    private String roomLeader;
    private String newChatRoomName;

    public static ChatRoomUpdateDto createChatRoomUpdateDto(Long roomId, String roomLeader, String newChatRoomName) {
        return ChatRoomUpdateDto.builder()
                .roomId(roomId)
                .roomLeader(roomLeader)
                .newChatRoomName(newChatRoomName)
                .build();
    }
}
