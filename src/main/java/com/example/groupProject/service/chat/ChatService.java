package com.example.groupProject.service.chat;

import com.example.groupProject.dto.chat.ChatMessageDto;
import com.example.groupProject.dto.chat.MessageSubDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final RedisPublisher redisPublisher;

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessageDto chatMessage) {
//        // 0. redis에 해당 채팅방roomId(key)에 마지막 메세지(value)를 넣어준다.
//        chatRoomRedisRepository.setLastChatMessage(chatMessage.getRoomId(), chatMessage);
//
//        Long userId = chatMessage.getUserId();
//        Long partnerId;
//
//        // 1. 채팅방이 삭제되는 것이라면 delete 를 해준다.
//        if (chatMessage.getType().equals(MessageType.DELETE)) {
//            chatRoomService.deleteChatRoom(accessToken, chatMessage.getRoomId(), userId);
//            chatRoomRedisRepository.deleteChatRoom(userId,chatMessage.getRoomId());
//        }
//
//        ChatRoomListGetResponse newChatRoomList = null;
//        if (chatRoomRedisRepository.existChatRoom(userId, chatMessage.getRoomId())) {
//            newChatRoomList = chatRoomRedisRepository.getChatRoom(userId, chatMessage.getRoomId());
//        } else {
//            newChatRoomList = chatRoomService.getChatRoomInfo(accessToken, chatMessage.getRoomId());
//        }
//
//        partnerId = getPartnerId(chatMessage, newChatRoomList);
//
//        // 2. 채팅방 리스트에 새로운 채팅방 정보가 없다면, 넣어준다. 마지막 메시지도 같이 담는다. 상대방 레디스에도 업데이트 해준다.
//        setNewChatRoomInfo(chatMessage, newChatRoomList);
//
//        // 3. 마지막 메시지들이 담긴 채팅방 리스트들을 가져온다.
//        List<ChatRoomListGetResponse> chatRoomListGetResponseList = chatRoomService.getChatRoomList(userId, accessToken);
//        // 4. 파트너 채팅방 리스트도 가져온다. (파트너는 userId 로만)
//        List<ChatRoomListGetResponse> partnerChatRoomGetResponseList = getChatRoomListByUserId(partnerId);
//
//        // 5. 마지막 메세지 기준으로 정렬 채팅방 리스트 정렬
//        chatRoomListGetResponseList = chatRoomService.sortChatRoomListLatest(chatRoomListGetResponseList);
//        partnerChatRoomGetResponseList = chatRoomService.sortChatRoomListLatest(partnerChatRoomGetResponseList);

        MessageSubDto messageSubDto = MessageSubDto.builder()
                .chatMessageDto(chatMessage)
                .build();

        redisPublisher.publish(messageSubDto);
    }
}
