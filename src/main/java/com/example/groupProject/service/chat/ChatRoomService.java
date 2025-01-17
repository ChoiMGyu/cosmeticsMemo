package com.example.groupProject.service.chat;

import com.example.groupProject.dto.chat.ChatRoomAllDto;

public interface ChatRoomService {

    ChatRoomAllDto findAllChatRoom();

    Long createChatRoom(String roomLeader, String roomName);


}
