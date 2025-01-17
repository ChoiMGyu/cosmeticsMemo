package com.example.groupProject.repository.chat;

import com.example.groupProject.domain.chat.ChatRoom;
import com.example.groupProject.dto.chat.ChatRoomDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findChatRoomByName(String name);

    @Query("SELECT new com.example.groupProject.dto.chat.ChatRoomDto(c.roomName, u.name, c.userCount) " +
            "FROM ChatRoom c " +
            "JOIN User u ON u.id = c.roomLeaderId")
    List<ChatRoomDto> findAllChatRoomWithLeaderName();
}
