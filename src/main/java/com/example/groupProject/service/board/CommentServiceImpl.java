package com.example.groupProject.service.board;

import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.board.Comment;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.board.comment.CommentDto;
import com.example.groupProject.dto.board.comment.CommentReadDto;
import com.example.groupProject.dto.board.comment.CommentUpdateDto;
import com.example.groupProject.repository.board.BoardRepository;
import com.example.groupProject.repository.board.CommentRepository;
import com.example.groupProject.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private static final String NOT_EXIST_BOARD = "선택하신 게시물의 ID가 존재하지 않습니다.";
    private static final String NOT_EXIST_COMMENT = "수정할 댓글이 존재하지 않습니다.";

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

        commentRepository.save(comment);

        return comment.getId();
    }

    @Override
    public void update(Long boardId, CommentUpdateDto commentUpdateDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_BOARD));

        Comment comment = commentRepository.findById(commentUpdateDto.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_COMMENT));

        comment.isSameWriter(commentUpdateDto.getWriter());

        comment.changeContent(commentUpdateDto.getContent());
    }

    @Override
    public void delete(Long boardId, Long commentId, String writer) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_BOARD));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_COMMENT));

        comment.isSameWriter(writer);

        commentRepository.delete(comment);
    }

    @Override
    public List<CommentReadDto> findAll(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_BOARD));

        List<Comment> comments = commentRepository.findAllByBoardId(boardId);

        return comments.stream()
                .map(CommentReadDto::from)
                .collect(Collectors.toList());
    }


}
