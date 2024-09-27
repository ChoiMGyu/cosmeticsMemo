package com.example.groupProject.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users") //H2 DB는 user가 예약어
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String account; //아이디 (닉네임 겸용)

    private String password; //패스워드

    private LocalDate birthdate; //생년월일

    @Enumerated(EnumType.STRING)
    private SkinType skinType; //피부 타입 ex) 건성, 지성, 복합성, 수부지

    private boolean notification_opt = false; //웹 푸시 알림 허용

    private boolean sex; //성별 (남자는 True, 여자는 false)

    public static User createUser(String account, String password, LocalDate birthdate, SkinType skinType, Boolean notification_opt, Boolean sex) {
        User user = new User();
        user.initialAccount(account);
        user.initialPassword(password);
        user.initialBirth(birthdate);
        user.initialSkinType(skinType);
        user.initialNotification_opt(notification_opt);
        user.initialSex(sex);
        return user;
    }

    private void initialAccount(String account) {
        this.account = account;
    }

    private void initialPassword(String password) {
        this.password = password;
    }

    private void initialBirth(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    private void initialSkinType(SkinType skinType) {
        this.skinType = skinType;
    }

    private void initialNotification_opt(Boolean notification_opt) {
        this.notification_opt = notification_opt;
    }

    private void initialSex(Boolean sex) {
        this.sex = sex;
    }

    // 패스워드 업데이트 메서드
    public void updatePassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }
}
