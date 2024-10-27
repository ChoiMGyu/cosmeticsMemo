package com.example.groupProject.service;
import com.example.groupProject.domain.user.RoleType;
import com.example.groupProject.domain.user.SkinType;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.repository.UserRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;  // JUnit 5 애너테이션
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    UserRepositoryImpl userRepository;
    @Autowired
    EntityManager em;

    @BeforeEach
    void setUp() {
        User user = User.createUser(
                "account",
                "pwd",
                LocalDate.now(),
                SkinType.DRY,
                true,
                true,
                RoleType.ROLE_USER
        );

        userService.join(user);
    }

    @Test
    @DisplayName(value = "회원 가입")
    public void 회원가입() throws Exception {
        //given

        //when
        List<User> account = userRepository.findByAccount("account");

        //then
        assertThat(account.get(0).getAccount()).isEqualTo("account");
    }

    @Test
    @DisplayName("아이디가 동일한 경우 예외를 발생시킨다.")
    public void 중복_회원_예외() throws Exception
    {
        //given
        User user = User.createUser(
                "account",
                "pwd",
                LocalDate.now(),
                SkinType.DRY,
                true,
                true,
                RoleType.ROLE_USER
        );

        //when
        //userService.join(user);

        //then
        assertThrows(IllegalStateException.class, () -> {
            userService.join(user);
        });
    }

}
