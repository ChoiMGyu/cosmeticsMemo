package com.example.groupProject.service.chat;

import com.example.groupProject.domain.chat.ChatRoom;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.chat.ChatMessageDto;
import com.example.groupProject.dto.chat.ChatRoomAllDto;
import com.example.groupProject.dto.chat.ChatRoomDto;
import com.example.groupProject.dto.chat.ChatRoomUpdateDto;
import com.example.groupProject.repository.chat.ChatRoomRepository;
import com.example.groupProject.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService {
    private static final String NOT_EXIST_USER = "로그인 후 진행해 주세요.";
    private static final String EXIST_CHATROOM_NAME = "중복된 채팅방 이름입니다.";
    private static final String CREATE_CHATROOM_MESSAGE = "채팅방을 개설하였습니다.";
    private static final String NOT_EXIST_CHAT_ROOM = "채팅방이 존재하지 않습니다.";
    private static final String DELETE_CHATROOM_MESSAGE = "채팅방이 삭제되었습니다.";
    private static final String UPDATE_CHATROOM_NAME_MESSAGE = "채팅방 이름이 변경되었습니다.";
    private static final String NOT_ROOM_LEADER_ERROR = "채팅방의 방장이 아닙니다.";

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public ChatRoomAllDto findAllChatRoom() {
        List<ChatRoomDto> chatRooms = chatRoomRepository.findAllChatRoomWithLeaderName();

        return ChatRoomAllDto.from(chatRooms);
    }

    @Override
    @Transactional
    public Long createChatRoom(String roomLeader, String roomName) {
        List<User> user = userRepository.findByAccount(roomLeader);
        if (user.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_USER);
        }

        chatRoomRepository.findChatRoomByRoomName(roomName)
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

        chatRoomRepository.save(chatRoom);

        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .type(ChatMessageDto.MessageType.CREATE)
                .roomId(Long.toString(chatRoom.getId()))
                .userId(user.getLast().getId())
                .message(CREATE_CHATROOM_MESSAGE)
                .time(formattedTime)
                .userCount(chatRoom.getUserCount())
                .build();

        kafkaProducerService.objectMapperMessage(chatMessageDto);

        return chatRoom.getId();
    }

    @Override
    @Transactional
    public void deleteChatRoom(Long id, String roomLeader) {
        List<User> user = userRepository.findByAccount(roomLeader);
        if (user.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_USER);
        }

        ChatRoom chatRoom = chatRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_CHAT_ROOM));

        if (!chatRoom.getRoomLeaderId().equals(user.getFirst().getId())) {
            throw new IllegalArgumentException(NOT_ROOM_LEADER_ERROR);
        }

        chatRoomRepository.delete(chatRoom);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = LocalTime.now().format(formatter);

        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .type(ChatMessageDto.MessageType.DELETE)
                .roomId(Long.toString(chatRoom.getId()))
                .userId(user.getLast().getId())
                .message(DELETE_CHATROOM_MESSAGE)
                .time(formattedTime)
                .userCount(0)
                .build();

        kafkaProducerService.objectMapperMessage(chatMessageDto);
    }

    @Override
    @Transactional
    public void updateChatRoomName(ChatRoomUpdateDto chatRoomUpdateDto) {
        List<User> user = userRepository.findByAccount(chatRoomUpdateDto.getRoomLeader());
        if (user.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_USER);
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomUpdateDto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_CHAT_ROOM));

        if (!chatRoom.getRoomLeaderId().equals(user.getFirst().getId())) {
            throw new IllegalArgumentException(NOT_ROOM_LEADER_ERROR);
        }

        chatRoom.changeRoomName(chatRoomUpdateDto.getNewChatRoomName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = LocalTime.now().format(formatter);

        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .type(ChatMessageDto.MessageType.UPDATE_NAME)
                .roomId(Long.toString(chatRoom.getId()))
                .userId(user.getLast().getId())
                .message(UPDATE_CHATROOM_NAME_MESSAGE)
                .time(formattedTime)
                .userCount(chatRoom.getUserCount())
                .build();

        kafkaProducerService.objectMapperMessage(chatMessageDto);
    }
}
