package com.example.groupProject.service_unit;

import com.example.groupProject.domain.user.RoleType;
import com.example.groupProject.domain.user.SkinType;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.repository.user.UserRepository;
import com.example.groupProject.service.UserServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityManager em;

    @InjectMocks
    private UserServiceImpl userService;

    @Spy
    private User user;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    public void setUp() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        userService = new UserServiceImpl(userRepository, bCryptPasswordEncoder);
        user = spy(User.createUser(
                "account",
                "pwd",
                LocalDate.now(),
                SkinType.DRY,
                true,
                true,
                RoleType.ROLE_USER
        ));
    }

    @Test
    @DisplayName("회원 가입을 진행한다")
    public void 회원가입() throws Exception {
        //given
        when(user.getId()).thenReturn(1L);
        when(userRepository.save(any(User.class))).thenReturn(user);

        //when
        Long id = userService.join(user);

        //then
        assertThat(id).isEqualTo(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("아이디가 동일한 경우 예외를 발생시킨다.")
    public void 중복_회원_예외() throws Exception {
        //given
        User user1 = User.createUser(
                "account",
                "pwd",
                LocalDate.now(),
                SkinType.DRY,
                true,
                true,
                RoleType.ROLE_USER
        );

        User user2 = User.createUser(
                "account",
                "pwd",
                LocalDate.now(),
                SkinType.DRY,
                true,
                true,
                RoleType.ROLE_USER
        );

        userService.join(user1);
        when(userRepository.findByAccount("account")).thenReturn(List.of(user1));

        //when

        //then
        assertThrows(IllegalStateException.class, () -> {
            userService.join(user2);
        });
        verify(userRepository, times(1)).save(user1);
        verify(userRepository, times(0)).save(user2);
    }
}
