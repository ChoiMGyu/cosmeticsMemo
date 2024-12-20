package com.example.groupProject.dto.board;

import com.example.groupProject.dto.memo.SkincareDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BoardAllDto {
    private List<BoardDto> skincareDto;

    public static BoardAllDto from(List<BoardDto> boardDto) {
        return new BoardAllDto(boardDto);
    }
}
