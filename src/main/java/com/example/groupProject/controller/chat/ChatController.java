package com.example.groupProject.controller.chat;

import com.example.groupProject.dto.chat.ChatMessageDto;
import com.example.groupProject.service.chat.ChatMongoService;
import com.example.groupProject.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatMongoService chatMongoService;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto chatMessageDto, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        String accessToken = (String) sessionAttributes.get("accessToken");

        log.info("ChatController에서 사용자로부터 가져온 AccessToken ::: " + accessToken);

        chatMongoService.saveMessage(chatMessageDto);
        chatService.sendChatMessage(chatMessageDto, accessToken);
    }
}
