package com.example.groupProject.service.board;

import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.board.Likes;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.repository.board.BoardRepository;
import com.example.groupProject.repository.board.LikesRepository;
import com.example.groupProject.repository.user.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikesServiceImpl implements LikesService {
    private static final String NOT_EXIST_BOARD = "좋아요를 누를 수 없는 게시물입니다.";
    private static final String NOT_EXIST_USER = "로그인한 사용자만 좋아요를 누를 수 있습니다.";

    private final RedisTemplate<String, Integer> redisTemplate;
    private final LikesRepository likesRepository;
    private final BoardRepository boardRepository;
    private final UserRepositoryImpl userRepository;

    @Override
    @Transactional
    public void incrementLike(Long boardId, String account) {
        User findUser = userRepository.findByAccount(account).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_USER));

        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_BOARD));

        likesRepository.findByUserAndBoard(findUser, findBoard)
                .map(existingLike -> {
                    findBoard.increment();
                    return existingLike;
                })
                .orElseGet(() -> {
                    Likes like = Likes.builder()
                            .user(findUser)
                            .board(findBoard)
                            .build();
                    likesRepository.save(like);
                    findBoard.increment();
                    return like;
                });
    }

    @Override
    @Transactional
    public void decrementLike(Long boardId, String account) {
        User findUser = userRepository.findByAccount(account).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_USER));

        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_BOARD));

        likesRepository.findByUserAndBoard(findUser, findBoard)
                .ifPresent(like -> {
                    likesRepository.delete(like);
                    findBoard.decrement();
                });
    }
}
