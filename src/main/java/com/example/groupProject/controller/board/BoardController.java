package com.example.groupProject.controller.board;

import com.example.groupProject.controller.message.ErrorMessage;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.board.BoardAllDto;
import com.example.groupProject.dto.board.BoardDto;
import com.example.groupProject.dto.board.BoardPageDto;
import com.example.groupProject.dto.jwt.CustomUserDetails;
import com.example.groupProject.service.UserServiceImpl;
import com.example.groupProject.service.board.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {
    private static final int BOARD_WRITER = 0;
    private static final String SUCCESS_CREATE_BOARD_MESSAGE = "게시물을 성공적으로 게시하였습니다.";
    private static final String SUCCESS_DELETE_BOARD_MESSAGE = "게시물을 성공적으로 삭제하였습니다.";

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    private final BoardService boardService;
    private final UserServiceImpl userService;

    @GetMapping("/board")
    public ResponseEntity<BoardAllDto> findAllBoards(@RequestBody BoardPageDto boardPageDto) {
        Page<BoardDto> boardPage = boardService.findAllBoardPagingByMasterId(boardPageDto);
        return ResponseEntity.status(HttpStatus.OK).body(BoardAllDto.from(boardPage.getContent()));
    }

    @PostMapping("/board")
    public ResponseEntity<String> createBoard(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody BoardDto boardDto) {
        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.LOGIN_REQUIRED_MESSAGE.getMessage());
        }

        List<User> user = userService.findByAccount(customUserDetails.getUsername());

        boardService.saveBoard(boardDto, user.get(BOARD_WRITER));

        return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_CREATE_BOARD_MESSAGE);
    }

    @DeleteMapping("/board")
    public ResponseEntity<String> deleteSkincareMemo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     @RequestParam(value = "id") Long id) {

        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.LOGIN_REQUIRED_MESSAGE.getMessage());
        }

        boardService.deleteByIdBoard(id, customUserDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_DELETE_BOARD_MESSAGE);
    }

    @PutMapping("/board")
    public ResponseEntity<String> updateBoard(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @RequestParam(value = "id") Long id,
                                              @RequestBody BoardDto boardDto) {

        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.LOGIN_REQUIRED_MESSAGE.getMessage());
        }

        boardService.updateBoard(id, boardDto, customUserDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_DELETE_BOARD_MESSAGE);
    }
}