package com.example.groupProject.service.chat;

import com.example.groupProject.dto.chat.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private static final String TOPIC = "chatting";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(ChatMessageDto chatMessageDto) {
        try {
            log.info("Produce message : {}", chatMessageDto.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            String message = objectMapper.writeValueAsString(chatMessageDto);
            this.kafkaTemplate.send(TOPIC, message);
        } catch (Exception e) {
            log.warn("Producer send Message ::: {}", e.getMessage());
        }
    }

}
