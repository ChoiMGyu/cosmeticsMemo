package com.example.groupProject.service.board;

import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.board.Likes;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.repository.board.BoardRepository;
import com.example.groupProject.repository.board.LikesRepository;
import com.example.groupProject.repository.user.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikesServiceImpl implements LikesService {
    private static final int LIKE_TTL_HOURS = 1;
    private static final int LIKE_INITIAL_COUNT = 0;
    private static final String NOT_EXIST_BOARD = "좋아요를 누를 수 없는 게시물입니다.";
    private static final String NOT_EXIST_USER = "로그인한 사용자만 좋아요를 누를 수 있습니다.";
    private static final String ALREADY_LIKE = "좋아요는 게시물 당 한 번만 누를 수 있습니다.";
    private static final String NOT_ALREADY_LIKE = "좋아요를 누르지 않은 게시물입니다.";
    private static final String REDIS_LIKE_USER_KEY = "board_users:";

    private final RedisTemplate<String, String> redisUserTemplate;
    private final LikesRepository likesRepository;
    private final BoardRepository boardRepository;
    private final UserRepositoryImpl userRepository;

    @Override
    @Transactional
    public void incrementLike(Long boardId, String account) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_BOARD));

        User findUser = userRepository.findByAccount(account)
                .stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_USER));

        String redisKey = REDIS_LIKE_USER_KEY + boardId;

        Boolean alreadyLiked = redisUserTemplate.opsForSet().isMember(redisKey, account);
        if (Boolean.TRUE.equals(alreadyLiked)) {
            log.info("Redis에 저장된 사용자 - 좋아요 증가");
            throw new IllegalArgumentException(ALREADY_LIKE);
        }

        likesRepository.findByUserAndBoard(findUser, findBoard)
                .ifPresent(like -> {
                    log.info("DB에 저장된 사용자 - 좋아요 증가");
                    throw new IllegalArgumentException(ALREADY_LIKE);
                });

        redisUserTemplate.opsForSet().add(redisKey, account);
        redisUserTemplate.expire(redisKey, LIKE_TTL_HOURS, TimeUnit.HOURS);

        Likes likes = Likes.builder()
                .board(findBoard)
                .user(findUser)
                .build();
        likesRepository.save(likes);
    }

    @Override
    @Transactional
    public void decrementLike(Long boardId, String account) {
        if (!boardRepository.existsById(boardId)) {
            throw new IllegalArgumentException(NOT_EXIST_BOARD);
        }

        String redisKey = REDIS_LIKE_USER_KEY + boardId;

        Boolean alreadyLiked = redisUserTemplate.opsForSet().isMember(redisKey, account);
        if (Boolean.TRUE.equals(alreadyLiked)) {
            redisUserTemplate.opsForSet().remove(redisKey, account);
            likesRepository.findByAccountJoinFetch(account).ifPresent(likesRepository::delete);
        } else {
            likesRepository.findByAccountJoinFetch(account)
                    .stream()
                    .findFirst()
                    .ifPresentOrElse(likesRepository::delete,
                            () -> {
                                throw new IllegalArgumentException(NOT_ALREADY_LIKE);
                            });
        }
    }

    private <T> T withFallback(Supplier<T> primary, Supplier<T> fallback) {
        try {
            return primary.get();
        } catch (Exception e) {
            log.warn("주 작업(Redis 읽기) 실패, 대체 작업(DB 읽기) 수행 중: {}", e.getMessage());
            return fallback.get();
        }
    }

    private Integer getLikesFromDatabaseAndCache(String redisKey, Long boardId) {
        boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_BOARD));
        Set<Likes> likesAtBoard = likesRepository.findByBoardIdJoinFetch(boardId);

        try {
            log.info("DB로부터 좋아요 개수를 가져왔기 때문에 Redis 업데이트가 필요");
            for (Likes likes : likesAtBoard) {
                redisUserTemplate.opsForSet().add(redisKey, likes.getUser().getAccount());
            }
            redisUserTemplate.expire(redisKey, LIKE_TTL_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("Redis에 좋아요 수 설정 실패: {}", e.getMessage());
        }

        return likesAtBoard.size();
    }

    @Override
    public int getLikesCount(Long boardId) {
        String redisKey = REDIS_LIKE_USER_KEY + boardId;

        return withFallback(
                () -> {
                    log.info("Redis로부터 좋아요 개수 읽기");
                    Integer likeCount = Optional.ofNullable(redisUserTemplate.opsForSet().size(redisKey))
                            .map(Long::intValue)
                            .orElse(LIKE_INITIAL_COUNT);

                    redisUserTemplate.expire(redisKey, LIKE_TTL_HOURS, TimeUnit.HOURS);

                    return likeCount;
                },
                () -> getLikesFromDatabaseAndCache(redisKey, boardId)
        );
    }

    private void updateLikesCountToDatabase(Long boardId, Integer likeCount) {
        log.info("Job으로 업데이트될 좋아요의 개수: " + likeCount);
        boardRepository.addLikeCount(boardId);
    }

    @Override
    @Transactional
    public void syncLikesToDatabase() {
        Set<String> keys = redisUserTemplate.keys(REDIS_LIKE_USER_KEY + "*");

        if (keys != null) {
            for (String key : keys) {
                Long boardId = Long.parseLong(key.replace(REDIS_LIKE_USER_KEY, ""));
                Integer likeCount = Optional.ofNullable(redisUserTemplate.opsForSet().size(key))
                        .map(Long::intValue)
                        .orElse(LIKE_INITIAL_COUNT);

                Board board = boardRepository.findById(boardId)
                        .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_BOARD));

                if (board.likeCountUpdateCompare(likeCount)) {
                    updateLikesCountToDatabase(boardId, likeCount);
                }
            }
        }
    }


}
