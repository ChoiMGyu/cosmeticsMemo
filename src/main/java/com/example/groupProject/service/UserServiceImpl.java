package com.example.groupProject.service;

import com.example.groupProject.domain.User;
import com.example.groupProject.repository.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepositoryImpl userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(User user) {
        //회원 가입
        //중복된 이메일을 사용하고 있는지 검사
        //패스워드 입력 일치 여부는 컨트롤러에서 검사
        validateDuplicateAccount(user);

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.updatePassword(encryptedPassword);

        userRepository.save(user);
        return user.getId();
    }

    private void validateDuplicateAccount(User user) {
        List<User> findMembers = userRepository.findByAccount(user.getAccount());
        if(!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 아아디입니다.");
        }
    }


}
