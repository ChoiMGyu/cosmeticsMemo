package com.example.groupProject.service;

import com.example.groupProject.domain.user.User;
import com.example.groupProject.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl {
    private static final String NOT_EXIST_USER = "존재하지 않는 사용자입니다.";
    private static final String EXIST_USER_MESSAGE = "이미 존재하는 아아디입니다.";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Long join(User user) {
        validateDuplicateAccount(user);

        String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.updatePassword(encryptedPassword);

        userRepository.save(user);
        return user.getId();
    }

    private void validateDuplicateAccount(User user) {
        List<User> findMembers = userRepository.findByAccount(user.getAccount());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException(EXIST_USER_MESSAGE);
        }
    }

    public List<User> findByAccount(String account) {
        return userRepository.findByAccount(account);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_USER));
    }
}
