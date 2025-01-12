package com.example.groupProject.service.chat;

import com.example.groupProject.dto.chat.ChatMessageDto;

public interface ChatMongoService {

    void saveMessage(ChatMessageDto chatMessageDto);
}
