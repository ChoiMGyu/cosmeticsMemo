package com.example.groupProject.dto.jwt;

import com.example.groupProject.domain.User.User;
import lombok.Getter;

@Getter
public class UserAdapter extends CustomUserDetails{

    private User user;

    public UserAdapter(User user) {
        super(user);
        this.user = user;
    }
}
