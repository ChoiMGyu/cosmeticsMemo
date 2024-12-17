package com.example.groupProject.service.board;

import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.board.BoardDto;
import com.example.groupProject.dto.board.BoardPageDto;
import org.springframework.data.domain.Page;

public interface BoardService {

    Long saveBoard(BoardDto boardDto, User user);

    BoardDto findById(Long id);

    void deleteByIdBoard(Long id);

    void updateBoard(Long id, BoardDto boardDto);

    Page<BoardDto> findAllBoardPagingByMasterId(BoardPageDto boardPageDto);
}
