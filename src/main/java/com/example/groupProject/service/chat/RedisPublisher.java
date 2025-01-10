package com.example.groupProject.service.chat;

import com.example.groupProject.dto.chat.MessageSubDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final ChannelTopic topic;
    private final RedisTemplate<String, Object> template;

    /** publish를 호출하면, topic을 구독하는 모든 구독자에게 message가 발행 (pub) */

    /**
     * Object publish
     */
    public void publish(MessageSubDto message) {
        log.info("RedisPublisher - topic ::: {}, message(Object) ::: {} ", topic.getTopic(), message);
        template.convertAndSend(topic.getTopic(), message);
    }

    /**
     * String publish
     */
    public void publish(String data) {
        log.info("RedisPublisher - topic ::: " + topic.getTopic() + ", message(String) ::: ", data);
        template.convertAndSend(topic.getTopic(), data);
    }
}
