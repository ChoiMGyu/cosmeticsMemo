package com.example.groupProject.controller.kafka;

import com.example.groupProject.dto.chat.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 3,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092"
        },
        ports = {9092})
class EmbeddedKafkaTest {

    @Autowired
    private KafkaProducer producer;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private KafkaConsumer consumer;

    @Test
    void test() throws Exception {
        // given
        String sendMessage = "카프카 테스트를 위해 전송한 메시지입니다,";
        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .roomId("1L")
                .message(sendMessage)
                .build();

        // when
        producer.sendMessage(chatMessageDto);

        // then
        boolean await = consumer.getLatch().await(2, TimeUnit.SECONDS);
        assertThat(await).isTrue();
        assertThat(consumer.getMessageQueue().size()).isEqualTo(1);
        assertThat(consumer.getMessageQueue().poll()).isEqualTo(sendMessage);
    }
}
