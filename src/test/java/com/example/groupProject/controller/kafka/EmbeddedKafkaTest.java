package com.example.groupProject.controller.kafka;

import com.example.groupProject.dto.chat.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("Kafka를 사용하여 메시지를 전송하고, foo라는 group이 이벤트를 수신한다")
    public void 카프카_메시지전송() throws Exception {
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
