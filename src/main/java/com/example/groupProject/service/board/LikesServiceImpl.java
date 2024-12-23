package com.example.groupProject.service.board;

import com.example.groupProject.domain.user.User;
import com.example.groupProject.repository.board.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {

    private final RedisTemplate<String, Integer> redisTemplate;
    private final LikesRepository likesRepository;

    @Override
    public void incrementLike(Long boardId, User user) {

    }
}
