package com.example.groupProject.repository.board;

import com.example.groupProject.domain.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {

    @Modifying(clearAutomatically = true)
    @Query("update Board b set b.like = :likeCount where b.id = :boardId")
    void addLikeCount(@Param("boardId") Long boardId, @Param("likeCount") Integer likeCount);
}
