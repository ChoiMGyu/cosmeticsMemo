package com.example.groupProject.dto.board.comment;

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
public class CommentUpdateDto {
    private Long commentId;

    private String writer;

    @NotBlank(message = "수정할 댓글 내용은 1자 이상 입력해야 합니다.")
    private String content;

    public static CommentUpdateDto create(Long commentId, String writer, String content) {
        return CommentUpdateDto.builder()
                .commentId(commentId)
                .writer(writer)
                .content(content)
                .build();
    }
}
