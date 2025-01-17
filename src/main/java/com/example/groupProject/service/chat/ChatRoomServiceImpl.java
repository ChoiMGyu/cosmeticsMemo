package com.example.groupProject.service.chat;

import com.example.groupProject.domain.chat.ChatRoom;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.chat.ChatMessageDto;
import com.example.groupProject.dto.chat.ChatRoomAllDto;
import com.example.groupProject.dto.chat.ChatRoomDto;
import com.example.groupProject.repository.chat.ChatRoomRepository;
import com.example.groupProject.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private static final String NOT_EXIST_USER = "로그인 후 진행해 주세요.";
    private static final String EXIST_CHATROOM_NAME = "중복된 채팅방 이름입니다.";
    private static final String CREATE_CHATROOM_MESSAGE = "채팅방을 개설하였습니다.";

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public ChatRoomAllDto findAllChatRoom() {
        List<ChatRoomDto> chatRooms = chatRoomRepository.findAllChatRoomWithLeaderName();

        return ChatRoomAllDto.from(chatRooms);
    }

    @Override
    public Long createChatRoom(String roomLeader, String roomName) {
        List<User> user = userRepository.findByAccount(roomLeader);
        if (user.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_USER);
        }

        chatRoomRepository.findChatRoomByName(roomName)
                .ifPresent(chatRoom -> {
                    throw new IllegalArgumentException(EXIST_CHATROOM_NAME);
                });

        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(roomName)
                .roomLeaderId(user.getFirst().getId())
                .userCount(0)
                .build();
        chatRoom.enterRoom();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = LocalTime.now().format(formatter);

        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .roomId(Long.toString(chatRoom.getId()))
                .userId(user.getLast().getId())
                .message(CREATE_CHATROOM_MESSAGE)
                .time(formattedTime)
                .userCount(chatRoom.getUserCount())
                .build();

        kafkaProducerService.sendMessage(chatMessageDto);

        chatRoomRepository.save(chatRoom);

        return chatRoom.getId();
    }
}
