package com.example.groupProject.controller;

import com.example.groupProject.annotation.WithMockCustomUser;
import com.example.groupProject.config.filter.JWTFilter;
import com.example.groupProject.controller.board.BoardController;
import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.board.BoardDto;
import com.example.groupProject.dto.board.BoardPageDto;
import com.example.groupProject.service.UserServiceImpl;
import com.example.groupProject.service.board.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BoardController.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private BoardService boardService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Board board;

    @BeforeEach
    public void setUp() {
        user = User.createUser("account_test", "password", null, null, null, null, null);
        userService.join(user);

        board = spy(Board.builder()
                .title("게시글 제목")
                .content("게시글 내용")
                .like(0)
                .hit(0)
                .createdAt(LocalDate.now())
                .master(user)
                .build());

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    @DisplayName("게시글을 올바르게 저장한다")
    @WithMockCustomUser
    public void 게시글_저장() throws Exception {
        //given
        BoardDto boardDto = BoardDto.from(board);
        when(boardService.saveBoard(any(BoardDto.class), any(User.class)))
                .thenReturn(1L);
        when(userService.findByAccount(anyString()))
                .thenReturn(List.of(user));

        String content = objectMapper.writeValueAsString(boardDto);

        //when

        //then
        mockMvc.perform(post("/api/boards/board")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글을 올바르게 업데이트한다")
    @WithMockCustomUser
    public void 게시글_업데이트() throws Exception {
        //given
        Long boardId = 1L;
        BoardDto updateBoardDto = BoardDto.builder()
                .title("수정할 게시글 제목")
                .content("수정할 게시글 내용")
                .build();

        doNothing().when(boardService).updateBoard(eq(boardId), any(BoardDto.class), anyString());

        String content = objectMapper.writeValueAsString(updateBoardDto);

        //when

        //then
        mockMvc.perform(put("/api/boards/board")
                        .param("id", String.valueOf(boardId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(boardService).updateBoard(eq(boardId), any(BoardDto.class), anyString());
    }

    @Test
    @DisplayName("게시글을 올바르게 삭제한다")
    @WithMockCustomUser
    public void 게시글_삭제() throws Exception {
        //given
        Long boardId = 1L;

        doNothing().when(boardService).deleteByIdBoard(eq(boardId), anyString());

        //when

        //then
        mockMvc.perform(delete("/api/boards/board")
                        .param("id", String.valueOf(boardId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(boardService).deleteByIdBoard(eq(boardId), anyString());
    }

    @Test
    @DisplayName("게시글을 정렬하여 확인할 수 있다")
    @WithMockUser
    public void 게시글_정렬_읽기() throws Exception {
        //given
        Long masterId = 1L;
        int page = 0;
        int size = 10;
        String sortBy = "register";

        BoardPageDto boardPageDto = BoardPageDto.builder()
                .masterId(masterId)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .build();

        Board board1 = Board.builder()
                .title("게시글 제목1")
                .content("게시글 내용1")
                .like(0)
                .hit(0)
                .createdAt(LocalDate.now().minusDays(1))
                .build();

        List<BoardDto> boardDtos = List.of(
                BoardDto.from(board), BoardDto.from(board1)
        );

        PageImpl<BoardDto> boardDtoPage = new PageImpl<>(boardDtos, PageRequest.of(page, size), boardDtos.size());

        when(boardService.findAllBoardPagingByMasterId(any(BoardPageDto.class))).thenReturn(boardDtoPage);

        String content = objectMapper.writeValueAsString(boardPageDto);

        //when

        //then
        mockMvc.perform(get("/api/boards/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andDo(print());

        verify(boardService).findAllBoardPagingByMasterId(any(BoardPageDto.class));
    }


}
