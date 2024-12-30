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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private RedisTemplate<String, String> redisUserTemplate;

    @Mock
    private SetOperations<String, String> setOperations;

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

        when(redisUserTemplate.opsForSet()).thenReturn(setOperations);
    }

    private static Stream<Arguments> provideIncrementLikeTestCase() {
        return Stream.of(
                Arguments.of("사용자가 좋아요를 처음 눌렀을 때(Redis - X, DB - X) 게시글의 좋아요 수가 증가한다", false, false, false),
                Arguments.of("사용자가 좋아요를 이미 누른 경우(Redis - O, DB - X)에는 좋아요 수는 변하지 않는다", true, false, true),
                Arguments.of("사용자가 좋아요를 이미 누른 경우(Redis - X, DB - O)에는 게시글의 좋아요 수는 변하지 않는다", false, true, true),
                Arguments.of("사용자가 좋아요를 이미 누른 경우(Redis - O, DB - O)에는 게시글의 좋아요 수는 변하지 않는다", true, true, true)
        );
    }

    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("provideIncrementLikeTestCase")
    @DisplayName("게시글 좋아요 증가 테스트")
    public void 게시글_좋아요_증가(String description, boolean redisExist, boolean dbExist, boolean isExceptionThrow) throws Exception {
        //given
        long findBoardId = 1L;
        String redisKey = "board_users:" + findBoardId;
        when(boardRepository.findById(findBoardId)).thenReturn(Optional.of(board));
        when(userRepository.findByAccount(anyString())).thenReturn(List.of(user));

        when(setOperations.isMember(eq(redisKey), anyString())).thenReturn(redisExist);

        if (!redisExist) {
            if (dbExist) {
                when(likesRepository.findByUserAndBoard(any(User.class), any(Board.class)))
                        .thenReturn(Optional.of(Likes.builder().user(user).board(board).build()));
            } else {
                when(likesRepository.findByUserAndBoard(any(User.class), any(Board.class))).thenReturn(Optional.empty());
            }
        }

        //when
        //then
        if (isExceptionThrow) {
            assertThrows(IllegalArgumentException.class,
                    () -> likesService.incrementLike(findBoardId, user.getAccount()));
        } else {
            likesService.incrementLike(findBoardId, user.getAccount());
            verify(redisUserTemplate.opsForSet(), times(1)).add(eq(redisKey), eq(user.getAccount()));
            verify(likesRepository, times(1)).save(any(Likes.class));
        }
    }

    private static Stream<Arguments> provideDecrementLikeTestCase() {
        return Stream.of(
                Arguments.of("사용자가 좋아요를 누른 경우(Redis - O, DB - O)에만 숫자가 감소한다", true, true, false),
                Arguments.of("사용자가 좋아요를 누른 경우(Redis - O, DB - X)에만 숫자가 감소한다", true, false, false),
                Arguments.of("사용자가 좋아요를 누른 경우(Redis - X, DB - O)에만 숫자가 감소한다", false, true, false),
                Arguments.of("사용자가 좋아요를 누르지 않았을 경우(Redis - X, DB - X)에는 숫자가 감소하지 않는다", false, false, true)
        );
    }

    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("provideDecrementLikeTestCase")
    @DisplayName("게시글 좋아요 감소 테스트")
    public void 게시글_좋아요_감소(String description, boolean redisExist, boolean dbExist, boolean isNotAlreadyLike) {
        // given
        long findBoardId = 1L;
        String redisKey = "board_users:" + findBoardId;

        when(boardRepository.existsById(findBoardId)).thenReturn(true);
        when(setOperations.isMember(redisKey, user.getAccount())).thenReturn(redisExist);

        if (dbExist) {
            when(likesRepository.findByAccountJoinFetch(user.getAccount()))
                    .thenReturn(Optional.of(Likes.builder().user(user).board(board).build()));
        } else {
            when(likesRepository.findByAccountJoinFetch(user.getAccount())).thenReturn(Optional.empty());
        }

        // when
        // then
        if (isNotAlreadyLike) {
            assertThrows(IllegalArgumentException.class,
                    () -> likesService.decrementLike(findBoardId, user.getAccount()));
        } else {
            likesService.decrementLike(findBoardId, user.getAccount());
            if (redisExist) {
                verify(redisUserTemplate.opsForSet(), times(1)).remove(redisKey, user.getAccount());
            }
            if (dbExist) {
                verify(likesRepository, times(1)).delete(any(Likes.class));
            }
        }
    }

    @Test
    @DisplayName("Redis에 저장된 게시글의 좋아요 수를 DB와 동기화한다")
    public void Redis_DB_동기화() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("게시글의 좋아요 수를 Redis 또는 DB에서 읽어온다")
    public void Redis_DB_좋아요수읽기() throws Exception {
        //given

        //when

        //then
    }

}
