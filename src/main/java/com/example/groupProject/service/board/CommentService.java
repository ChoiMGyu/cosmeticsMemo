package com.example.groupProject.service.board;

import com.example.groupProject.dto.board.CommentDto;

import java.util.List;

public interface CommentService {

    Long save(Long boardId, CommentDto commentDto);
}
