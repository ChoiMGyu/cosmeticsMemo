package com.example.groupProject.dto.board;

import com.example.groupProject.domain.board.Board;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {

    @NotBlank(message = "제목은 필수 입력 사항입니다.")
    private String title;

    @NotBlank(message = "내용은 한 글자 이상 입력 해야 합니다.")
    private String content;

    private Integer hit;
    private Integer like;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static BoardDto from(Board board) {
        return BoardDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .hit(board.getHit())
                .like(board.getLike())
                .createdAt(board.getCreatedAt())
                .modifiedAt(board.getModifiedAt())
                .build();
    }
}
