package com.example.groupProject.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class TokenDto {
    private String accessToken;
    private String refreshToken;
}
