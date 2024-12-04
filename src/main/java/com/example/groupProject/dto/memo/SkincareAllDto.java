package com.example.groupProject.dto.memo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SkincareAllDto {
    private String message;
    private String writer;
    private List<SkincareDto> skincareDto;
    private int count;

    public static SkincareAllDto of(String message, String writer, List<SkincareDto> skincareDto, int count) {
        return new SkincareAllDto(message, writer, skincareDto, count);
    }
}
