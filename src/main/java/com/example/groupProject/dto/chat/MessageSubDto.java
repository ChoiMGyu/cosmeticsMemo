package com.example.groupProject.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageSubDto {
    private Long userId;
    //private Long partnerId;
    private ChatMessageDto chatMessageDto;
    //private List<ChatRoomListGetResponse> list;
    //private List<ChatRoomListGetResponse> partnerList;
}
