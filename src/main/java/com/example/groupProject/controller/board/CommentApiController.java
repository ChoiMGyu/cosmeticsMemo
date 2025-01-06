package com.example.groupProject.controller.board;

import com.example.groupProject.controller.message.ErrorMessage;
import com.example.groupProject.dto.board.comment.CommentDto;
import com.example.groupProject.dto.board.comment.CommentUpdateDto;
import com.example.groupProject.dto.jwt.CustomUserDetails;
import com.example.groupProject.service.board.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentApiController {
    private static final String SUCCESS_CREATE_COMMENT_MESSAGE = "게시물에 댓글을 성공적으로 달았습니다.";
    private static final String SUCCESS_UPDATE_COMMENT_MESSAGE = "게시물에 댓글을 성공적으로 수정했습니다.";
    private static final String SUCCESS_DELETE_COMMENT_MESSAGE = "게시물에 댓글을 성공적으로 삭제했습니다.";

    private final CommentService commentService;

    @PostMapping("/{boardId}/comment")
    public ResponseEntity<String> createComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                @PathVariable(value = "boardId") Long boardId,
                                                @Valid @RequestBody CommentDto commentDto) {
        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.LOGIN_REQUIRED_MESSAGE.getMessage());
        }

        commentService.save(boardId, commentDto);

        return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_CREATE_COMMENT_MESSAGE);
    }

    @PutMapping("/{boardId}/comment")
    public ResponseEntity<String> updateBoard(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @PathVariable(value = "boardId") Long boardId,
                                              @Valid @RequestBody CommentUpdateDto commentUpdateDto) {

        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.LOGIN_REQUIRED_MESSAGE.getMessage());
        }

        commentService.update(boardId, commentUpdateDto);

        return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_UPDATE_COMMENT_MESSAGE);
    }

    @DeleteMapping("/{boardId}/comment/{commentId}")
    public ResponseEntity<String> deleteSkincareMemo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     @PathVariable(value = "boardId") Long boardId,
                                                     @PathVariable(value = "commentId") Long commentId) {

        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.LOGIN_REQUIRED_MESSAGE.getMessage());
        }

        commentService.delete(boardId, commentId, customUserDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_DELETE_COMMENT_MESSAGE);
    }
}
