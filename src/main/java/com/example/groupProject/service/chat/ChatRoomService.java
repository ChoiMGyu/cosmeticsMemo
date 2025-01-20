package com.example.groupProject.service.chat;

import com.example.groupProject.dto.chat.ChatRoomAllDto;
import com.example.groupProject.dto.chat.ChatRoomUpdateDto;

public interface ChatRoomService {

    ChatRoomAllDto findAllChatRoom();

    Long createChatRoom(String roomLeader, String roomName);

    void deleteChatRoom(Long id, String roomLeader);

    void updateChatRoomName(ChatRoomUpdateDto chatRoomUpdateDto);
}
