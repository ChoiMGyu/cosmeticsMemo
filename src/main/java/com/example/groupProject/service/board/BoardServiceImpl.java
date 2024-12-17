package com.example.groupProject.service.board;

import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.board.BoardDto;
import com.example.groupProject.dto.board.BoardPageDto;
import com.example.groupProject.repository.board.BoardRepository;
import com.example.groupProject.repository.board.BoardSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {
    private static final String NOT_EXIST_BOARD = "선택하신 게시물의 ID가 존재하지 않습니다.";

    private final BoardRepository boardRepository;


    @Override
    @Transactional
    public Long saveBoard(BoardDto boardDto, User user) {
        Board board = Board.builder()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .master(user)
                .build();

        boardRepository.save(board);
        return board.getId();
    }

    @Override
    public BoardDto findById(Long id) {
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_BOARD));

        return BoardDto.from(findBoard);
    }

    @Override
    @Transactional
    public void deleteByIdBoard(Long id) {
        if (!boardRepository.existsById(id)) {
            throw new IllegalArgumentException(NOT_EXIST_BOARD);
        }
        boardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateBoard(Long id, BoardDto boardDto) {
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_BOARD));
        findBoard.changeBoard(boardDto.getTitle(), boardDto.getContent());
    }

    @Override
    public Page<BoardDto> findAllBoardPagingByMasterId(BoardPageDto boardPageDto) {
        Pageable pageable = PageRequest.of(boardPageDto.getPage(), boardPageDto.getSize());

        Specification<Board> spec = Specification.where(BoardSpecifications.withMasterId(boardPageDto.getMasterId()))
                .and(BoardSpecifications.sortBy(boardPageDto.getSortBy()));

        Page<Board> skincareMemoPaging = boardRepository.findAll(spec, pageable);

        return skincareMemoPaging.map(BoardDto::from);
    }

}
