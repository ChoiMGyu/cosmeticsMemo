package com.example.groupProject.controller;

import com.example.groupProject.annotation.WithMockCustomUser;
import com.example.groupProject.controller.board.CommentApiController;
import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.board.Comment;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.board.comment.CommentDto;
import com.example.groupProject.dto.board.comment.CommentUpdateDto;
import com.example.groupProject.service.UserServiceImpl;
import com.example.groupProject.service.board.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CommentApiController.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class CommentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Board board;
    private List<Comment> comments = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        user = User.createUser("account_test", "password", null, null, null, null, null);
        userService.join(user);

        board = spy(Board.builder()
                .title("게시글 제목")
                .content("게시글 내용")
                .hit(0)
                .master(user)
                .build());

        for (int i = 0; i < 3; i++) {
            Comment comment = spy(Comment.builder()
                    .content("댓글입니다 + " + i)
                    .board(board)
                    .master(user)
                    .build());
            comments.add(comment);
        }

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    @DisplayName("게시글에 댓글을 저장한다")
    @WithMockCustomUser
    public void 게시글_댓글_저장() throws Exception {
        //given
        Long boardId = 1L;
        CommentDto commentDto = CommentDto.from(comments.get(0));
        String content = objectMapper.writeValueAsString(commentDto);

        when(commentService.save(any(Long.class), any(CommentDto.class))).thenReturn(boardId);

        //when

        //then
        mockMvc.perform(post("/api/comments/{boardId}/comment", boardId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글에 작성된 댓글을 수정한다")
    @WithMockCustomUser
    public void 댓글_수정() throws Exception {
        //given
        Long boardId = 1L;
        Long commentId = 1L;
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.create(commentId, "account_test", "수정된 댓글입니다.");
        String content = objectMapper.writeValueAsString(commentUpdateDto);

        doNothing().when(commentService).update(any(Long.class), any(CommentUpdateDto.class));

        //when

        //then
        mockMvc.perform(put("/api/comments/{boardId}/comment", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(commentService).update(eq(boardId), any(CommentUpdateDto.class));
    }

}
