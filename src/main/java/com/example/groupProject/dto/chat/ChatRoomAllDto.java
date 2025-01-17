package com.example.groupProject.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatRoomAllDto {
    List<ChatRoomDto> chatRoomDtos;

    public static ChatRoomAllDto from(List<ChatRoomDto> chatRoomDtos) {
        return new ChatRoomAllDto(chatRoomDtos);
    }
}
