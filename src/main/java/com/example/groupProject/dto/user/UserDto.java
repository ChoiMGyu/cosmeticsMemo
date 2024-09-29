package com.example.groupProject.dto.user;

import com.example.groupProject.domain.User.SkinType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(message = "아이디는 필수 입력입니다.") // null, 빈 문자열, 공백 허용하지 않음
    private String account; // 아이디 (닉네임 겸용)

    @NotBlank(message = "패스워드는 필수 입력입니다.") // null, 빈 문자열, 공백 허용하지 않음
    private String password; // 패스워드

    @NotBlank(message = "패스워드 확인란은 필수 입력입니다.") // null, 빈 문자열, 공백 허용하지 않음
    private String password_chk; // 패스워드 확인

    @NotNull(message = "생년월일은 필수 입력입니다.") // null 허용하지 않음
    private LocalDate birthdate; // 생년월일

    @NotNull(message = "피부 타입은 필수 입력입니다.") // null 허용하지 않음
    private SkinType skinType; // 피부 타입 ex) 건성, 지성, 복합성, 수부지

    @NotNull(message = "웹 푸시 알림 허용 여부는 필수입니다.")
    private Boolean notification_opt; // 웹 푸시 알림 허용

    @NotNull(message = "성별은 필수입니다.")
    private Boolean sex; // 성별
}
