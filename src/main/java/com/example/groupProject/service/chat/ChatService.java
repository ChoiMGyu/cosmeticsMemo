package com.example.groupProject.service.chat;

import com.example.groupProject.domain.chat.ChatMessage;
import com.example.groupProject.dto.chat.ChatMessageDto;
import com.example.groupProject.dto.chat.MessageSubDto;
import com.example.groupProject.mongo.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final RedisPublisher redisPublisher;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessageDto chatMessageDto) {
        //채팅방을 생성한다 (ENTER) -> 특정 채널의 roomId를 구독한다
        switch (chatMessageDto.getType()) {
            case ENTER:
                handleEnter(chatMessageDto);
                break;
            case TALK:
                handleTalk(chatMessageDto);
                break;
            default:
                log.warn("Unknown Message Type : {}", chatMessageDto.getType());
        }

        //채팅방에서 대화를 시도한다 (TALK)

        //채팅방에서 나가려고 한다 (QUIT)

        //채팅방을 삭제하려고 한다 (DELETE)
    }

    /**
     * 채팅방 입장 처리
     */
    private void handleEnter(ChatMessageDto chatMessageDto) {
        // 입장 시 userCount 증가
        chatMessageDto.setUserCount(chatMessageDto.getUserCount() + 1);

        // 메시지 내용 설정
        chatMessageDto.setMessage(chatMessageDto.getUserId() + "님이 채팅방에 입장했습니다.");

        MessageSubDto messageSubDto = MessageSubDto.builder()
                .userId(chatMessageDto.getUserId())
                .chatMessageDto(chatMessageDto)
                .build();

        //ChatMessage를 생성하여 mongoDB에 저장한다

        // Redis를 통해 입장 메시지 발행
        redisPublisher.publish(messageSubDto);

        log.info("handleEnter - User {} entered room {}", chatMessageDto.getUserId(), chatMessageDto.getRoomId());
    }

    /**
     * 채팅방에서 대화 시 처리
     */
    private void handleTalk(ChatMessageDto chatMessageDto) {
        // 대화 메시지를 Redis에 발행
        MessageSubDto messageSubDto = MessageSubDto.builder()
                .userId(chatMessageDto.getUserId())
                .chatMessageDto(chatMessageDto)
                .build();

        redisPublisher.publish(messageSubDto);
        log.info("handleTalk - User {} sent message to room {}: {}", chatMessageDto.getUserId(), chatMessageDto.getRoomId(), chatMessageDto.getMessage());
    }

    /**
     * 채팅방에서 퇴장 처리 -> 구독 해제가 필요하지 않을까?
     */
    private void handleQuit(ChatMessageDto chatMessageDto) {
        chatMessageDto.setUserCount(chatMessageDto.getUserCount() - 1);
        chatMessageDto.setMessage(chatMessageDto.getUserId() + "님이 채팅방에서 나갔습니다.");

        MessageSubDto messageSubDto = MessageSubDto.builder()
                .userId(chatMessageDto.getUserId())
                .chatMessageDto(chatMessageDto)
                .build();

        redisPublisher.publish(messageSubDto);

        log.info("handleQuit - User {} left room {}", chatMessageDto.getUserId(), chatMessageDto.getRoomId());
    }

    /**
     * 채팅방 삭제 처리 -> 구독 해제가 필요하지 않을까?
     */
    private void handleDelete(ChatMessageDto chatMessageDto) {
        log.info("Message deleted in room {}", chatMessageDto.getRoomId());
    }
}
