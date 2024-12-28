package com.example.groupProject.service.board;

public interface LikesService {

    void incrementLike(Long boardId, String account);

    void decrementLike(Long boardId, String account);

    void syncLikesToDatabase();
}
