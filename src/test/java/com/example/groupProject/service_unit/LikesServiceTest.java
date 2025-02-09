package com.example.groupProject.service_unit;

import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.board.Likes;
import com.example.groupProject.domain.user.RoleType;
import com.example.groupProject.domain.user.SkinType;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.repository.board.BoardRepository;
import com.example.groupProject.repository.board.LikesRepository;
import com.example.groupProject.repository.user.UserRepository;
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
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("tests")
public class LikesServiceTest {

    @Mock
    private LikesRepository likesRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

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
    }

    private static Stream<Arguments> provideLikeTestCase() {
        return Stream.of(
                Arguments.of("사용자가 좋아요를 처음 눌렀을 때(Redis - X, DB - X) 게시글의 좋아요 수가 증가한다", false, false, false),
                Arguments.of("사용자가 좋아요를 이미 누른 경우(Redis - O, DB - X) 게시글의 좋아요 수는 감소한다", true, false, true),
                Arguments.of("사용자가 좋아요를 이미 누른 경우(Redis - X, DB - O) 게시글의 좋아요 수는 감소한다", false, true, true),
                Arguments.of("사용자가 좋아요를 이미 누른 경우(Redis - O, DB - O) 게시글의 좋아요 수는 감소한다", true, true, true)
        );
    }

    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("provideLikeTestCase")
    @DisplayName("게시글 좋아요 테스트")
    public void 게시글_좋아요_테스트(String description, boolean redisExist, boolean dbExist) {
        //given
        long findBoardId = 1L;
        String redisKey = "board_users:" + findBoardId;

        when(redisUserTemplate.opsForSet()).thenReturn(setOperations);

        when(boardRepository.findById(findBoardId)).thenReturn(Optional.of(board));
        when(userRepository.findByAccount(anyString())).thenReturn(List.of(user));

        when(setOperations.isMember(eq(redisKey), anyString())).thenReturn(redisExist);
        if (dbExist) {
            when(likesRepository.findByUserAndBoard(any(User.class), any(Board.class)))
                    .thenReturn(Optional.of(Likes.builder().user(user).board(board).build()));
        } else {
            when(likesRepository.findByUserAndBoard(any(User.class), any(Board.class))).thenReturn(Optional.empty());
        }

        //when
        //then
        likesService.doLike(findBoardId, user.getAccount());
        if (redisExist || dbExist) {
            if (redisExist) {
                verify(redisUserTemplate.opsForSet(), times(1)).remove(redisKey, user.getAccount());
            }
            if (dbExist) {
                verify(likesRepository, times(1)).delete(any(Likes.class));
            }
        } else {
            verify(redisUserTemplate.opsForSet(), times(1)).add(eq(redisKey), eq(user.getAccount()));
            verify(likesRepository, times(1)).save(any(Likes.class));
        }
    }

    private static Stream<Arguments> provideSyncLikesToDatabaseTestCases() {
        return Stream.of(
                Arguments.of(
                        "Redis에 게시글이 존재하지 않는 경우",
                        null,
                        null,
                        null,
                        null
                ),
                Arguments.of(
                        "Redis와 DB의 좋아요 수가 동일한 경우",
                        Set.of("board_users:1"),
                        true,
                        1,
                        1
                ),
                Arguments.of(
                        "Redis와 DB의 좋아요 수가 다른 경우",
                        Set.of("board_users:1", "board_users:2"),
                        false,
                        2,
                        2
                )
        );
    }

    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("provideSyncLikesToDatabaseTestCases")
    @DisplayName("Redis와 DB의 좋아요 수를 동기화 한다")
    public void Redis_DB_동기화(String description, Set<String> keys, Boolean isSameRedisDB, Integer expectedLikeCount, Integer repeatTime) throws Exception {
        //given
        when(redisUserTemplate.keys(anyString())).thenReturn(keys);

        Board findBoard = spy(Board.builder()
                .title("Test Board")
                .content("Test Content")
                .master(user)
                .like(1)
                .build());

        if (keys != null) {
            for (String key : keys) {
                Long boardId = Long.parseLong(key.replace("board_users:", ""));
                when(redisUserTemplate.opsForSet()).thenReturn(setOperations);
                when(redisUserTemplate.opsForSet().size(key)).thenReturn(Long.valueOf(keys.size()));
                when(boardRepository.findById(boardId)).thenReturn(Optional.of(findBoard));
                when(findBoard.likeCountUpdateCompare(repeatTime)).thenReturn(isSameRedisDB);
            }
        }

        //when
        //then
        likesService.syncLikesToDatabase();
        if (keys != null && !isSameRedisDB) {
            findBoard.changeLikeCount(keys.size());
            assertThat(findBoard.getLike()).isEqualTo(expectedLikeCount);
        }
    }

    @Test
    @DisplayName("게시글의 좋아요 수를 Redis 또는 DB에서 읽어온다")
    public void Redis_DB_좋아요수읽기() throws Exception {
        //given

        //when

        //then
    }

}
