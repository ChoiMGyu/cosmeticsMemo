package com.example.groupProject.service.authService;

import com.example.groupProject.dto.jwt.TokenDto;

public interface JwtService {

    public void addRefresh(String account, String refreshToken);

    public TokenDto reissueToken(String refreshToken);
}
