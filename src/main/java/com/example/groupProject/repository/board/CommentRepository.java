package com.example.groupProject.repository.board;

import com.example.groupProject.domain.board.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.board.id = :boardId")
    List<Comment> findAllByBoardId(@Param(value = "boardId") Long boardId);
}
