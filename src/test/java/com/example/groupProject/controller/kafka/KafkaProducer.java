package com.example.groupProject.controller.kafka;

import com.example.groupProject.dto.chat.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private static final String TOPIC_NAME = "chatting";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(ChatMessageDto chatMessageDto) {
        try {
            log.info("test class - Produce message : {}", chatMessageDto.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            String payload = objectMapper.writeValueAsString(chatMessageDto);
            kafkaTemplate.send(TOPIC_NAME, payload);
        } catch (Exception e) {
            log.warn("test class - Producer send Message ::: {}", e.getMessage());
        }
    }
}