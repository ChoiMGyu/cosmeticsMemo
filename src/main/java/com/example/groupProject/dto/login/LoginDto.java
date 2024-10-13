package com.example.groupProject.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    @NotBlank(message = "아이디를 입력해 주세요.") // null, 빈 문자열, 공백 허용하지 않음
    private String account; // 아이디 (닉네임 겸용)

    @NotBlank(message = "패스워드를 입력해 주세요.") // null, 빈 문자열, 공백 허용하지 않음
    private String password; // 패스워드
}
