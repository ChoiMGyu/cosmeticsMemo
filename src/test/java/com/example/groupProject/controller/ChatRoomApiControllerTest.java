package com.example.groupProject.controller;

import com.example.groupProject.annotation.WithMockCustomUser;
import com.example.groupProject.controller.chat.ChatRoomApiController;
import com.example.groupProject.dto.chat.ChatRoomAllDto;
import com.example.groupProject.dto.chat.ChatRoomDto;
import com.example.groupProject.dto.chat.ChatRoomUpdateDto;
import com.example.groupProject.service.chat.ChatRoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatRoomApiController.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class ChatRoomApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatRoomService chatRoomService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setBeforeEach() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    @DisplayName("채팅방 리스트를 조회할 수 있다")
    @WithMockUser
    public void 채팅방_리스트_조회() throws Exception {
        //given
        List<ChatRoomDto> chatRoomDtos = Stream.of(
                new String[]{"채팅방 이름", "채팅방 방장"},
                new String[]{"채팅방 이름1", "채팅방 방장1"},
                new String[]{"채팅방 이름2", "채팅방 방장2"}
        ).map(data -> ChatRoomDto.builder()
                .roomName(data[0])
                .roomLeaderName(data[1])
                .userCount(1)
                .build()
        ).toList();

        ChatRoomAllDto chatRoomAllDto = ChatRoomAllDto.from(chatRoomDtos);

        when(chatRoomService.findAllChatRoom()).thenReturn(chatRoomAllDto);

        //when

        //then
        mockMvc.perform(get("/api/chatrooms/chatroomList"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("채팅방을 생성할 수 있다")
    @WithMockCustomUser
    public void 채팅방_생성() throws Exception {
        //given
        String roomName = "새로운 방이름";
        when(chatRoomService.createChatRoom(eq("account"), eq(roomName))).thenReturn(1L);

        //when

        //then
        mockMvc.perform(post("/api/chatrooms/chatroom")
                        .param("roomName", roomName)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("채팅방 이름을 수정할 수 있다")
    @WithMockCustomUser
    public void 채팅방_이름_수정() throws Exception {
        //given
        String newChatRoomName = "새로운 채팅방 이름";
        ChatRoomUpdateDto chatRoomUpdateDto = ChatRoomUpdateDto.builder()
                .roomId(1L)
                .roomLeader("채팅방 방장")
                .newChatRoomName(newChatRoomName)
                .build();

        doNothing().when(chatRoomService).updateChatRoomName(eq(chatRoomUpdateDto));

        String content = objectMapper.writeValueAsString(chatRoomUpdateDto);

        //when

        //then
        mockMvc.perform(put("/api/chatrooms/chatroom") // 경로가 올바르게 수정됨
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("채팅방을 삭제할 수 있다")
    @WithMockCustomUser
    public void 채팅방_삭제() throws Exception {
        //given
        Long id = 1L;
        String roomLeader = "채팅방 방장";

        doNothing().when(chatRoomService).deleteChatRoom(eq(id), eq(roomLeader));

        //when

        //then
        mockMvc.perform(delete("/api/chatrooms/chatroom")
                        .param("id", String.valueOf(id))
                        .param("roomLeader", roomLeader)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }


}
