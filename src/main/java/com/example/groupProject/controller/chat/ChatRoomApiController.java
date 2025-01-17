package com.example.groupProject.controller.chat;

import com.example.groupProject.controller.message.ErrorMessage;
import com.example.groupProject.dto.chat.ChatRoomAllDto;
import com.example.groupProject.dto.jwt.CustomUserDetails;
import com.example.groupProject.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatrooms")
public class ChatRoomApiController {
    private static final String SUCCESS_CREATE_CHATROOM_MESSAGE = "채팅방을 생성하였습니다.";

    private final ChatRoomService chatRoomService;

    @GetMapping("/chatroomList")
    public ResponseEntity<ChatRoomAllDto> findAllChatRooms() {
        ChatRoomAllDto chatRooms = chatRoomService.findAllChatRoom();

        return ResponseEntity.status(HttpStatus.OK).body(chatRooms);
    }

    @PostMapping("/chatroom")
    public ResponseEntity<String> createChatRoom(@AuthenticationPrincipal CustomUserDetails customUserDetails, String roomName) {
        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.LOGIN_REQUIRED_MESSAGE.getMessage());
        }

        chatRoomService.createChatRoom(customUserDetails.getUsername(), roomName);

        return ResponseEntity.status(HttpStatus.CREATED).body(SUCCESS_CREATE_CHATROOM_MESSAGE);
    }
}
