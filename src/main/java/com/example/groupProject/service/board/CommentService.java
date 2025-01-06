package com.example.groupProject.service.board;

import com.example.groupProject.dto.board.comment.CommentDto;
import com.example.groupProject.dto.board.comment.CommentReadDto;
import com.example.groupProject.dto.board.comment.CommentUpdateDto;

import java.util.List;

public interface CommentService {

    Long save(Long boardId, CommentDto commentDto);

    void update(Long boardId, CommentUpdateDto commentUpdateDto);

    void delete(Long boardId, Long commentId, String writer);

    List<CommentReadDto> findAll(Long boardId);
}
