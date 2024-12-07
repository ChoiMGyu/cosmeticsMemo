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

    public static SkincareAllDto of(String message, String writer, List<SkincareDto> skincareDto) {
        return new SkincareAllDto(message, writer, skincareDto);
    }
}
