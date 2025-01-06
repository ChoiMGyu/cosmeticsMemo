package com.example.groupProject.dto.board.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CommentAllDto {
    private List<CommentReadDto> comments;

    public static CommentAllDto from(List<CommentReadDto> comments) {
        return new CommentAllDto(comments);
    }
}
