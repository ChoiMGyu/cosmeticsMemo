package com.example.groupProject.controller.chat;

import com.example.groupProject.dto.chat.ChatMessageDto;
import com.example.groupProject.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    //private final ChatMongoService chatMongoService;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto chatMessageDto) {
        //mongoDB에 채팅방 저장 save
        chatService.sendChatMessage(chatMessageDto);
    }
}
