package com.example.groupProject.service.chat;

import com.example.groupProject.dto.chat.ChatMessageDto;
import com.example.groupProject.dto.chat.MessageSubDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Redis에서 메시지가 발행(publish)되면
     * 대기하고 있던 Redis Subscriber가 해당 메시지를 받아 처리한다.
     */
    public void sendMessage(String publishMessage) {
        try {
            ChatMessageDto chatMessageDto = objectMapper.readValue(publishMessage, MessageSubDto.class).getChatMessageDto();

            log.info("Redis Subcriber chatMSG : {}", chatMessageDto.getMessage());

            // 채팅방을 구독한 클라이언트에게 메시지 발송
            messagingTemplate.convertAndSend(
                    "/sub/chat/room/" + chatMessageDto.getRoomId(), chatMessageDto
            );
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
