package com.example.groupProject.service;

import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.user.RoleType;
import com.example.groupProject.domain.user.SkinType;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.repository.board.BoardRepository;
import com.example.groupProject.repository.board.LikesRepository;
import com.example.groupProject.repository.user.UserRepository;
import com.example.groupProject.service.board.LikesService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LikesServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private LikesService likesService;

    @Autowired
    private RedisTemplate<String, String> redisUserTemplate;

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    private List<User> users;
    private List<Board> boards;

    @BeforeEach
    void beforeEach() {
        boards = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Board board = Board.builder()
                    .title("게시글 제목")
                    .content("게시글 내용")
                    .hit(0)
                    .master(users.get(0))
                    .build();
            this.boards.add(board);
            boardRepository.save(board);
        }
    }

    @AfterEach
    public void afterEach() {
        likesRepository.deleteAll();
        boardRepository.deleteAll();
    }

    @BeforeAll
    public void beforeAll() {
        users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = User.createUser(
                    "account" + i,
                    "pwd",
                    LocalDate.now(),
                    SkinType.DRY,
                    true,
                    true,
                    RoleType.ROLE_USER
            );
            this.users.add(user);
            userRepository.save(user);
        }
    }

    @Test
    @DisplayName("같은 사용자가 같은 게시물에 좋아요를 두 번 누르면 좋아요가 기록되지 않는다")
    public void 좋아요_동시성() throws Exception {
        //given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        //when
        Long boardId = boards.get(0).getId();
        for (int i = 0; i < threadCount; i++) {
            int idx = i / 2;
            executorService.submit(() -> {
                try {
                    likesService.doLike(boardId, users.get(idx).getAccount());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        //then
        int findLikeCount = redisUserTemplate.opsForSet().size("board_users:" + boardId).intValue();
        assertThat(findLikeCount).isEqualTo(0);
    }

}

