package com.example.groupProject.repository.board;

import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.board.Likes;
import com.example.groupProject.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByUserAndBoard(User user, Board board);

    @Query("SELECT l FROM Likes l JOIN FETCH l.user u WHERE u.account = :account")
    Optional<Likes> findByAccountJoinFetch(@Param(value = "account") String account);

    @Query("SELECT l FROM Likes l join fetch l.board b WHERE b.id = :boardId")
    Set<Likes> findByBoardIdJoinFetch(@Param(value = "boardId") Long boardId);
}
