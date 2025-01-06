package com.example.groupProject.dto.board;

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
public class CommentDto {
    private String writer;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
                .content(comment.getContent())
                .createdDate(comment.getCreatedAt())
                .modifiedDate(comment.getModifiedAt())
                .writer(comment.getMaster().getAccount())
                .build();
    }
}
