package com.example.groupProject.service.board;

import com.example.groupProject.domain.user.User;

public interface LikesService {

    void incrementLike(Long boardId, User user);
}
