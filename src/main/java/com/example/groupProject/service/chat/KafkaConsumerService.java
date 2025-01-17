package com.example.groupProject.service.chat;

import com.example.groupProject.dto.chat.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private static final String TOPIC_NAME = "chatting";

    private final SimpMessageSendingOperations template;

    @KafkaListener(topics = TOPIC_NAME, groupId = "foo")
    public void consume(String messageJson) throws IOException {
        //토픽으로 메시지가 수신될 때 @KafkaListener 메서드가 실행
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ChatMessageDto message = objectMapper.readValue(messageJson, ChatMessageDto.class);
            template.convertAndSend("/sub/chat/room" + message.getRoomId(), message);
        } catch (Exception e) {
            throw new RuntimeException("Consumer read message ::: " + e.getMessage());
        }
    }
}
