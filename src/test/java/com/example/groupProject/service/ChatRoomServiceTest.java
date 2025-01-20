package com.example.groupProject.service;

import com.example.groupProject.domain.chat.ChatRoom;
import com.example.groupProject.domain.user.RoleType;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.chat.ChatMessageDto;
import com.example.groupProject.dto.chat.ChatRoomAllDto;
import com.example.groupProject.dto.chat.ChatRoomDto;
import com.example.groupProject.repository.chat.ChatRoomRepository;
import com.example.groupProject.repository.user.UserRepository;
import com.example.groupProject.service.chat.ChatRoomService;
import com.example.groupProject.service.chat.KafkaProducerService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EmbeddedKafka(partitions = 3,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092"
        },
        ports = {9092})
public class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    private User user;

    @BeforeEach
    public void setBeforeEach() {
        user = User.createUser("채팅방 방장", "password", null, null, null, null, RoleType.ROLE_USER);
        userRepository.save(user);
    }

    @AfterEach
    public void setAfterEach() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("채팅방을 생성할 수 있다")
    public void 채팅방_생성() throws Exception {
        //given
        String roomLeader = "채팅방 방장";
        String roomName = "채팅방 이름";

        //when
        Long chatRoomId = chatRoomService.createChatRoom(roomLeader, roomName);
        Optional<ChatRoom> findChatRoom = chatRoomRepository.findChatRoomByRoomName(roomName);
        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .roomId(Long.toString(chatRoomId))
                .userId(user.getId())
                .message("채팅방을 개설하였습니다.")
                .time(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .userCount(1)
                .build();

        //then
        assertThat(chatRoomId).isEqualTo(findChatRoom.get().getId());
        verify(kafkaProducerService, times(1)).sendMessage(chatMessageDto);
    }

    @Test
    @DisplayName("채팅방 리스트를 조회할 수 있다")
    public void 채팅방_리스트_조회() throws Exception {
        //given
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName("채팅방 이름")
                .roomLeaderId(user.getId())
                .userCount(1)
                .build();

        ChatRoom chatRoom1 = ChatRoom.builder()
                .roomName("채팅방 이름1")
                .roomLeaderId(user.getId())
                .userCount(1)
                .build();

        chatRoomRepository.save(chatRoom);
        chatRoomRepository.save(chatRoom1);

        //user가 만든 채팅방이 두 개(chatRoom, chatRoom1)

        User user1 = User.createUser("채팅방 방장1", "password", null, null, null, null, RoleType.ROLE_USER);
        userRepository.save(user1);

        ChatRoom chatRoom2 = ChatRoom.builder()
                .roomName("채팅방 이름2")
                .roomLeaderId(user1.getId())
                .userCount(1)
                .build();

        chatRoomRepository.save(chatRoom2);

        //user1이 만든 채팅방이 한 개(chatRoom2)

        //when
        ChatRoomAllDto allChatRoom = chatRoomService.findAllChatRoom();

        //then
        List<ChatRoomDto> chatRoomDtos = allChatRoom.getChatRoomDtos();
        assertThat(chatRoomDtos.size()).isEqualTo(3);
        assertThat(chatRoom.getRoomLeaderId()).isEqualTo(user.getId());
        assertThat(chatRoom1.getRoomLeaderId()).isEqualTo(user.getId());
        assertThat(chatRoom2.getRoomLeaderId()).isEqualTo(user1.getId());
    }

}
