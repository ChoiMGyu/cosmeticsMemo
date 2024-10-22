package com.example.groupProject.service.AuthService;

import com.example.groupProject.domain.User.User;
import com.example.groupProject.dto.jwt.CustomUserDetails;
import com.example.groupProject.dto.jwt.UserAdapter;
import com.example.groupProject.repository.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositoryImpl userRepository;

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        List<User> userList = userRepository.findByAccount(account);

        if(!userList.isEmpty()) {
            return new CustomUserDetails(userList.get(0));
            //return new UserAdapter(userList.get(0));
        }

        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
    }
}
