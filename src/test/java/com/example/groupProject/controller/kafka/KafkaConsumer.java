package com.example.groupProject.controller.kafka;

import com.example.groupProject.dto.chat.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.PriorityBlockingQueue;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
public class KafkaConsumer {
    private static final String TOPIC_NAME = "chatting";

    @Getter(AccessLevel.NONE)
    private final SimpMessageSendingOperations template;

    private CountDownLatch latch = new CountDownLatch(1);
    private BlockingQueue<String> messageQueue = new PriorityBlockingQueue<>();

    @KafkaListener(topics = TOPIC_NAME, groupId = "foo")
    protected void consume(@Payload String payload) throws Exception {
        log.info("test class - receive event : {}", payload);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ChatMessageDto message = objectMapper.readValue(payload, ChatMessageDto.class);
            template.convertAndSend("/sub/chat/room" + message.getRoomId(), message);
            if (messageQueue != null) {
                messageQueue.add(message.getMessage());
            }
            latch.countDown();
        } catch (Exception e) {
            throw new RuntimeException("test class - Consumer read message ::: " + e.getMessage());
        }
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
