package com.example.groupProject.domain.chat;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_message")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatMessage {

    @Id
    private String id;
    private Long userId;
    private String roomId;
    private String message;
    private String time;
    private long userCount;
}
