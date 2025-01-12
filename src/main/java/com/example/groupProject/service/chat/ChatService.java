package com.example.groupProject.service.chat;

import com.example.groupProject.domain.chat.ChatRoom;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.chat.ChatMessageDto;
import com.example.groupProject.dto.chat.MessageSubDto;
import com.example.groupProject.repository.chat.ChatRoomRepository;
import com.example.groupProject.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private static final String NOT_EXIST_USER = "존재하지 않는 회원입니다.";
    private static final String NOT_EXIST_CHATROOM = "존재하지 않는 채팅방입니다.";

    private final RedisPublisher redisPublisher;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessageDto chatMessageDto) {
        switch (chatMessageDto.getType()) {
            case ENTER:
                handleEnter(chatMessageDto);
                break;
            case TALK:
                handleTalk(chatMessageDto);
                break;
            case QUIT:
                handleQuit(chatMessageDto);
            default:
                log.warn("Unknown Message Type : {}", chatMessageDto.getType());
        }
    }

    /**
     * 채팅방 입장 처리
     */
    private void handleEnter(ChatMessageDto chatMessageDto) {
        //MessageType이 ENTER일 때

        //roomId를 클라이언트로부터 전달받음 (roomId 방에 속한 subscriber에게 메시지를 전달)

        //userId는 publisher로 누가 방을 만들었는지 출력하기 위해 User의 이름을 찾아야 함
        User user = userRepository.findById(chatMessageDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_USER));

        //message의 경우, 채팅방에 입장하였다는 걸 알려주어야 한다
        chatMessageDto.setMessage(user.getAccount() + "님이 채팅방에 입장하였습니다.");

        //time의 경우, 현재 메시지를 작성한 시간을 설정한다
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = LocalTime.now().format(formatter);
        chatMessageDto.setTime(formattedTime);

        //입장 시, 현재 속한 채팅방 인원을 증가해주어야 한다
        ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(chatMessageDto.getRoomId()))
                .orElseGet(() -> {
                    ChatRoom newChatRoom = ChatRoom.builder()
                            .userCount(0)
                            .build();
                    return chatRoomRepository.save(newChatRoom);
                });
        chatMessageDto.setUserCount(chatRoom.getUserCount());

        MessageSubDto messageSubDto = MessageSubDto.builder()
                .userId(chatMessageDto.getUserId())
                .chatMessageDto(chatMessageDto)
                .build();

        redisPublisher.publish(messageSubDto);

        log.info("handleEnter - User {} entered room {}", chatMessageDto.getUserId(), chatMessageDto.getRoomId());
    }

    /**
     * 채팅방에서 대화 시 처리
     */
    private void handleTalk(ChatMessageDto chatMessageDto) {
        //MessageType이 TALK일 때

        //roomId에 해당하는 채팅방에 속한 인원들과 대화를 시도한다
        ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(chatMessageDto.getRoomId()))
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_CHATROOM));
        chatMessageDto.setRoomId(chatMessageDto.getRoomId());

        //userId는 대화를 보낸 (publish) 사람의 이름이어야 한다
        User user = userRepository.findById(chatMessageDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_USER));

        //message는 user가 입력한 메시지를 그대로 전달한다
        chatMessageDto.setMessage(user.getAccount() + " : " + chatMessageDto.getMessage());

        //time은 현재 시간으로 설정한다
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = LocalTime.now().format(formatter);
        chatMessageDto.setTime(formattedTime);

        //userCount의 경우 변하지 않는다

        MessageSubDto messageSubDto = MessageSubDto.builder()
                .userId(chatMessageDto.getUserId())
                .chatMessageDto(chatMessageDto)
                .build();

        redisPublisher.publish(messageSubDto);

        log.info("handleTalk - User {} sent message to room {}: {}", chatMessageDto.getUserId(), chatMessageDto.getRoomId(), chatMessageDto.getMessage());
    }

    /**
     * 채팅방에서 퇴장 처리
     * 구독 해제는 클라이언트 측 (프론트) 에서 이루어진다
     */
    private void handleQuit(ChatMessageDto chatMessageDto) {
        //MessageType이 QUIT일 때

        //roomId에 속한 인원들에게 퇴장 메시지를 전송한다

        //userId를 가진 인원이 메시지를 전송해야 한다
        User user = userRepository.findById(chatMessageDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_USER));

        //message로 퇴장 멘트를 작성한다
        chatMessageDto.setMessage(user.getAccount() + "님이 채팅방에서 나갔습니다.");

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

        redisPublisher.publish(messageSubDto);

        log.info("handleQuit - User {} left room {}", chatMessageDto.getUserId(), chatMessageDto.getRoomId());
    }

    /**
     * 채팅방 삭제 처리
     */
    private void handleDelete(ChatMessageDto chatMessageDto) {
        //MessageType이 DELETE일 때

        log.info("Message deleted in room {}", chatMessageDto.getRoomId());
    }
}
