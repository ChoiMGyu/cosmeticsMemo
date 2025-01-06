package com.example.groupProject.service;

import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.board.Comment;
import com.example.groupProject.domain.user.RoleType;
import com.example.groupProject.domain.user.SkinType;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.board.CommentDto;
import com.example.groupProject.repository.board.BoardRepository;
import com.example.groupProject.repository.board.CommentRepository;
import com.example.groupProject.repository.board.LikesRepository;
import com.example.groupProject.repository.user.UserRepository;
import com.example.groupProject.service.board.CommentService;
import com.example.groupProject.service.board.LikesService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class CommentServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    private User user;
    private Board board;

    @BeforeEach
    public void beforeEach() {
        user = User.createUser(
                "account",
                "pwd",
                LocalDate.now(),
                SkinType.DRY,
                true,
                true,
                RoleType.ROLE_USER
        );
        userRepository.save(user);

        board = Board.builder()
                .title("게시글 제목")
                .content("게시글 내용")
                .hit(0)
                .master(user)
                .build();
        boardRepository.save(board);
    }

    @Test
    @DisplayName("게시글에 댓글을 생성할 수 있다")
    public void 댓글_생성() throws Exception
    {
        //given
        Comment comment = Comment.builder()
                .content("댓글입니다.")
                .board(board)
                .master(user)
                .build();

        CommentDto commentDto = CommentDto.from(comment);

        //when
        Long commentId = commentService.save(board.getId(), commentDto);

        //then
        Comment findComment = commentRepository.findById(commentId).get();
        assertThat(findComment.getContent()).isEqualTo(comment.getContent());
    }

    @Test
    @DisplayName("게시글에 작성된 댓글을 수정할 수 있다")
    public void 댓글_수정() throws Exception
    {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("게시글에 작성된 댓글을 삭제할 수 있다")
    public void 댓글_삭제() throws Exception
    {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("댓글 작성자가 아닌 사람이 수정 또는 삭제를 시도하면 오류가 발생한다")
    public void 댓글_작성자X_수정_삭제_시도() throws Exception
    {
        //given

        //when

        //then
    }


    @Test
    @DisplayName("게시글에 작성된 댓글을 모두 읽을 수 있다")
    public void 댓글_읽기() throws Exception
    {
        //given

        //when

        //then
    }

}
