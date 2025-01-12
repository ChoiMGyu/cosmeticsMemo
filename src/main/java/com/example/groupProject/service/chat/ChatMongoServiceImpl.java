package com.example.groupProject.service.chat;

import com.example.groupProject.domain.chat.ChatMessage;
import com.example.groupProject.dto.chat.ChatMessageDto;
import com.example.groupProject.mongo.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMongoServiceImpl implements ChatMongoService {

    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void saveMessage(ChatMessageDto chatMessageDto) {
        ChatMessage chatMessage = ChatMessage.builder()
                .userId(chatMessageDto.getUserId())
                .roomId(chatMessageDto.getRoomId())
                .message(chatMessageDto.getMessage())
                .time(chatMessageDto.getTime())
                .userCount(chatMessageDto.getUserCount())
                .build();

        chatMessageRepository.save(chatMessage);
    }
}
