package com.example.groupProject.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email; //아이디처럼 사용될 이메일

    private String password; //패스워드

    private String password_chk; //패스워드 확인

    private String name; //이름

    private LocalDate birthdate; //생년월일

    private SkinType skinType; //피부 타입 ex) 건성, 지성, 복합성, 수부지

    private boolean notification_opt; //웹 푸시 알림 허용

    private Sex sex; //성별
}
