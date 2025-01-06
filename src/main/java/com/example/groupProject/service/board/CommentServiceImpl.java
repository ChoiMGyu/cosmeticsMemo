package com.example.groupProject.service.board;

import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.board.Comment;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.board.CommentDto;
import com.example.groupProject.repository.board.BoardRepository;
import com.example.groupProject.repository.board.CommentRepository;
import com.example.groupProject.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private static final String NOT_EXIST_BOARD = "선택하신 게시물의 ID가 존재하지 않습니다.";

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Long save(Long boardId, CommentDto commentDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_BOARD));

        User user = userRepository.findByAccount(commentDto.getWriter()).getFirst();

        Comment comment = Comment.builder()
                .content(commentDto.getContent())
                .board(board)
                .master(user)
                .build();

        board.addComment(comment);
        commentRepository.save(comment);

        return comment.getId();
    }
}
