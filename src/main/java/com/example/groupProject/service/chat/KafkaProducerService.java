package com.example.groupProject.service.chat;

import com.example.groupProject.config.util.JWTUtil;
import com.example.groupProject.domain.chat.ChatRoom;
import com.example.groupProject.dto.chat.ChatMessageDto;
import com.example.groupProject.dto.chat.MessageSubDto;
import com.example.groupProject.repository.chat.ChatRoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private static final String TOPIC = "chatting";
    private static final String NOT_EXIST_CHATROOM = "채팅방이 존재하지 않습니다.";

    private final JWTUtil jwtUtil;
    private final ChatRoomRepository chatRoomRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(ChatMessageDto chatMessageDto, String accessToken) {
        try {
            log.info("Produce message : {}", chatMessageDto.getMessage());
            switch (chatMessageDto.getType()) {
                case ENTER:
                    handleEnter(chatMessageDto, accessToken);
                    break;
                case TALK:
                    handleTalk(chatMessageDto, accessToken);
                    break;
                case QUIT:
                    handleQuit(chatMessageDto, accessToken);
                default:
                    log.warn("Unknown Message Type : {}", chatMessageDto.getType());
            }
        } catch (Exception e) {
            log.warn("Producer send Message ::: {}", e.getMessage());
        }
    }

    public void objectMapperMessage(ChatMessageDto chatMessageDto) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String message = objectMapper.writeValueAsString(chatMessageDto);
            kafkaTemplate.send(TOPIC, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 채팅방 입장 처리
     */
    private void handleEnter(ChatMessageDto chatMessageDto, String accessToken) {
        //MessageType이 ENTER일 때

        //roomId를 클라이언트로부터 전달받음 (roomId 방에 속한 subscriber에게 메시지를 전달)

        //userId는 publisher로 누가 방을 만들었는지 출력하기 위해 User의 이름을 찾아야 함
        String account = jwtUtil.getAccount(accessToken);

        //message의 경우, 채팅방에 입장하였다는 걸 알려주어야 한다
        chatMessageDto.setMessage(account + "님이 채팅방에 입장하였습니다.");

        //time의 경우, 현재 메시지를 작성한 시간을 설정한다
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = LocalTime.now().format(formatter);
        chatMessageDto.setTime(formattedTime);

        //입장 시, 현재 속한 채팅방 인원을 증가해주어야 한다
        ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(chatMessageDto.getRoomId()))
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_CHATROOM));

        chatRoom.enterRoom();
        chatMessageDto.setUserCount(chatRoom.getUserCount());

        MessageSubDto messageSubDto = MessageSubDto.builder()
                .userId(chatMessageDto.getUserId())
                .chatMessageDto(chatMessageDto)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String message;
        try {
            message = objectMapper.writeValueAsString(messageSubDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        kafkaTemplate.send(TOPIC, message);

        log.info("handleEnter - User {} entered room {}", chatMessageDto.getUserId(), chatMessageDto.getRoomId());
    }

    /**
     * 채팅방에서 대화 시 처리
     */
    private void handleTalk(ChatMessageDto chatMessageDto, String accessToken) {
        //MessageType이 TALK일 때

        //roomId에 해당하는 채팅방에 속한 인원들과 대화를 시도한다
        ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(chatMessageDto.getRoomId()))
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_CHATROOM));
        chatMessageDto.setRoomId(chatMessageDto.getRoomId());

        //userId는 대화를 보낸 (publish) 사람의 이름이어야 한다
        String account = jwtUtil.getAccount(accessToken);

        //message는 user가 입력한 메시지를 그대로 전달한다
        chatMessageDto.setMessage(account + " : " + chatMessageDto.getMessage());

        //time은 현재 시간으로 설정한다
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = LocalTime.now().format(formatter);
        chatMessageDto.setTime(formattedTime);

        //userCount의 경우 변하지 않는다
        chatMessageDto.setUserCount(chatRoom.getUserCount());

        MessageSubDto messageSubDto = MessageSubDto.builder()
                .userId(chatMessageDto.getUserId())
                .chatMessageDto(chatMessageDto)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String message;
        try {
            message = objectMapper.writeValueAsString(messageSubDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        kafkaTemplate.send(TOPIC, message);

        log.info("handleTalk - User {} sent message to room {}: {}", chatMessageDto.getUserId(), chatMessageDto.getRoomId(), chatMessageDto.getMessage());
    }

    /**
     * 채팅방에서 퇴장 처리
     * 구독 해제는 클라이언트 측 (프론트) 에서 이루어진다
     */
    private void handleQuit(ChatMessageDto chatMessageDto, String accessToken) {
        //MessageType이 QUIT일 때

        //roomId에 속한 인원들에게 퇴장 메시지를 전송한다

        //userId를 가진 인원이 메시지를 전송해야 한다
        String account = jwtUtil.getAccount(accessToken);

        //message로 퇴장 멘트를 작성한다
        chatMessageDto.setMessage(account + "님이 채팅방에서 나갔습니다.");

        //time은 현재 시간으로 설정한다
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = LocalTime.now().format(formatter);
        chatMessageDto.setTime(formattedTime);

        //userCount는 감소시키고, 0이 될 경우 채팅방을 삭제한다
        chatRoomRepository.findById(Long.parseLong(chatMessageDto.getRoomId()))
                .map(chatRoom -> {
                    if (chatRoom.getUserCount() == 1) {
                        chatRoomRepository.delete(chatRoom);
                    } else {
                        chatRoom.quitRoom();
                    }
                    return chatRoom;
                })
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_CHATROOM));

        MessageSubDto messageSubDto = MessageSubDto.builder()
                .userId(chatMessageDto.getUserId())
                .chatMessageDto(chatMessageDto)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String message;
        try {
            message = objectMapper.writeValueAsString(messageSubDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        kafkaTemplate.send(TOPIC, message);

        log.info("handleQuit - User {} left room {}", chatMessageDto.getUserId(), chatMessageDto.getRoomId());
    }
}
