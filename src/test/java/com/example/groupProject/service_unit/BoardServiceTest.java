package com.example.groupProject.service_unit;

import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.board.BoardDto;
import com.example.groupProject.dto.board.BoardPageDto;
import com.example.groupProject.repository.board.BoardRepository;
import com.example.groupProject.service.board.BoardServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("tests")
public class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardServiceImpl boardService;

    private User user;

    @Spy
    private Board board;

    @BeforeEach
    void setUp() {
        user = User.createUser("account", "password", null, null, null, null, null);

        board = spy(Board.builder()
                .title("게시글 제목")
                .content("게시글 내용")
                .like(0)
                .hit(0)
                .register(LocalDate.now())
                .build());
    }

    @Test
    @DisplayName("게시글을 올바르게 저장한다")
    public void 게시글_저장() throws Exception {
        //given
        BoardDto boardDto = BoardDto.from(board);

        //when
        Long memoId = boardService.saveBoard(boardDto, user);

        //then
        verify(boardRepository, times(1)).save(any(Board.class));
    }

    @Test
    @DisplayName("게시글을 올바르게 삭제한다")
    public void 게시글_삭제() throws Exception {
        //given
        Long boardId = 1L;

        when(boardRepository.existsById(boardId)).thenReturn(true);
        doNothing().when(boardRepository).deleteById(boardId);

        //when
        boardService.deleteByIdBoard(boardId);

        //then
        verify(boardRepository).existsById(boardId);
        verify(boardRepository).deleteById(boardId);
    }

    @ParameterizedTest
    @DisplayName("게시글을 정렬 기준에 다라 페이징하여 반환한다")
    @CsvSource({
            "register, 게시글 제목, 나",
            "hit, 가, 나",
            "like, 가, 나"
    })
    public void 정렬기준_게시글_페이징(String sortBy, String expectedFirst, String expectedSecond) throws Exception {
        //given
        Board board1 = Board.builder()
                .title("가")
                .content("안녕하세요.처음 뵙겠습니다.")
                .like(2)
                .hit(2)
                .register(LocalDate.now().minusMonths(1))
                .build();

        Board board2 = Board.builder()
                .title("나")
                .content("Hi")
                .like(1)
                .hit(1)
                .register(LocalDate.now().minusDays(1))
                .build();

        List<Board> boards = List.of(board, board1, board2);
        Pageable pageable = PageRequest.of(0, 2);

        when(boardRepository.findAll(any(Specification.class), eq(pageable)))
                .thenAnswer(invocation -> {
                    Specification<Skincare> specification = invocation.getArgument(0);
                    Comparator<Board> comparator = switch (sortBy) {
                        case "register" -> Comparator.comparing(Board::getRegister).reversed();
                        case "hit" -> Comparator.comparing(Board::getHit).reversed();
                        case "like" -> Comparator.comparing(Board::getLike).reversed();
                        default -> throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다.");
                    };

                    List<Board> sortedBoards = boards.stream()
                            .sorted(comparator)
                            .collect(Collectors.toList());

                    int start = (int) pageable.getOffset();
                    int end = Math.min((start + pageable.getPageSize()), sortedBoards.size());
                    return new PageImpl<>(sortedBoards.subList(start, end), pageable, sortedBoards.size());
                });

        //when
        BoardPageDto boardPageDto = BoardPageDto.builder()
                .masterId(1L)
                .page(0)
                .size(2)
                .sortBy(sortBy)
                .build();

        Page<BoardDto> resultPage = boardService.findAllBoardPagingByMasterId(boardPageDto);

        //then
        Assertions.assertNotNull(resultPage);
        assertThat(resultPage.getContent().size()).isEqualTo(2);
        assertThat(resultPage.getContent().get(0).getTitle()).isEqualTo(expectedFirst);
        assertThat(resultPage.getContent().get(1).getTitle()).isEqualTo(expectedSecond);
    }

    @Test
    @DisplayName("게시글을 올바르게 수정한다")
    public void 게시글_수정() throws Exception
    {
        //given
        Long findBoardId = 1L;
        BoardDto updateDto = BoardDto.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        when(boardRepository.findById(findBoardId)).thenReturn(Optional.of(board));

        //when
        boardService.updateBoard(findBoardId, updateDto);

        //then
        verify(boardRepository, times(1)).findById(findBoardId);
        assertThat(board.getTitle()).isEqualTo("수정된 제목");
        assertThat(board.getContent()).isEqualTo("수정된 내용");
    }


}
