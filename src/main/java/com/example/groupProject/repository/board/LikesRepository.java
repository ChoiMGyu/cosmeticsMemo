package com.example.groupProject.repository.board;

import com.example.groupProject.domain.board.Board;
import com.example.groupProject.domain.board.Likes;
import com.example.groupProject.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByUserAndBoard(User user, Board board);
}
