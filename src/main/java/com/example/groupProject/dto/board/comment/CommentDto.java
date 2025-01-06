package com.example.groupProject.dto.board.comment;

import com.example.groupProject.domain.board.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @NotEmpty(message = "작성자는 필수 항목입니다.")
    private String writer;

    @NotBlank(message = "내용은 필수 항목입니다.")
    private String content;

    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
                .content(comment.getContent())
                .writer(comment.getMaster().getAccount())
                .build();
    }
}
