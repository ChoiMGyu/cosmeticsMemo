package com.example.groupProject.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomListGetResponse {
    //채팅방 내부의 필요한 값들

    private String username; //사용자 닉네임
    private String partnerName; //상대방 닉네임
    private String contentName; //상품 이름
    private String image; //상품 이미지
}
