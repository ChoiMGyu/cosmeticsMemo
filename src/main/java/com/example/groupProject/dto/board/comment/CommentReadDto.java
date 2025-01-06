package com.example.groupProject.dto.board.comment;

import com.example.groupProject.domain.board.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReadDto {
    private String writer;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static CommentReadDto from(Comment comment) {
        return CommentReadDto.builder()
                .writer(comment.getMaster().getAccount())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }
}
