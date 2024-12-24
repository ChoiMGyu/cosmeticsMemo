package com.example.groupProject.service_unit;

import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.board.Likes;
import com.example.groupProject.domain.user.RoleType;
import com.example.groupProject.domain.user.SkinType;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.repository.board.BoardRepository;
import com.example.groupProject.repository.board.LikesRepository;
import com.example.groupProject.repository.user.UserRepositoryImpl;
import com.example.groupProject.service.board.LikesServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("tests")
public class LikesServiceTest {

    @Mock
    private LikesRepository likesRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepositoryImpl userRepository;

    @InjectMocks
    private LikesServiceImpl likesService;

    @Spy
    private User user;

    @Spy
    private Board board;

    @BeforeEach
    public void setup() {
        user = User.createUser(
                "testAccount",
                "password123",
                LocalDate.of(2000, 1, 1),
                SkinType.DRY,
                true,
                true,
                RoleType.ROLE_USER
        );

        board = Board.builder()
                .title("Test Board")
                .content("Test Content")
                .master(user)
                .like(1)
                .build();
    }

    @Test
    @DisplayName("좋아요를 눌렀을 때 숫자가 증가한다")
    public void 좋아요수_증가() throws Exception {
        //given
        when(userRepository.findByAccount(any(String.class))).thenReturn(List.of(user));
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        //when
        likesService.incrementLike(1L, anyString());

        //then
        assertThat(board.getLike()).isEqualTo(2);
    }

    @Test
    @DisplayName("좋아요를 다시 눌렀을 때 숫자가 감소한다")
    public void 좋아요수_감소() throws Exception {
        //given
        when(userRepository.findByAccount(any(String.class))).thenReturn(List.of(user));
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        //when
        likesService.decrementLike(1L, anyString());

        //then
        assertThat(board.getLike()).isEqualTo(0);
    }

    @Test
    @DisplayName("좋아요를 누른 사용자가 저장된다")
    public void 좋아요_사용자_저장() throws Exception {
        //given

        //when

        //then
    }


    @Test
    @DisplayName("좋아요를 누른 사람을 찾을 수 있다")
    public void 좋아요_주인() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("게시글의 총 좋아요 수를 확인할 수 있다")
    public void 총_좋아요수() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("Redis에서 게시글에 대한 좋아요 수에 대한 데이터를 초기화한다")
    public void 좋아요_데이터_초기화() throws Exception {
        //given

        //when

        //then
    }


}
