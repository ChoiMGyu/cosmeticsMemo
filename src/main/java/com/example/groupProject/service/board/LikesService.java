package com.example.groupProject.service.board;

public interface LikesService {

    void increaseLikeLock(Long boardId, String account);

    void doLike(Long boardId, String account);

    int getLikesCount(Long boardId);

    void syncLikesToDatabase();
}
