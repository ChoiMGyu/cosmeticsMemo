package com.example.groupProject.repository.board;

import com.example.groupProject.domain.board.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {

}
