package com.example.groupProject.repository.memo;

import com.example.groupProject.domain.memo.Skincare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SkincareRepository extends JpaRepository<Skincare, Long> {

    @Query("SELECT s FROM Skincare s WHERE s.master.id = :id")
    List<Skincare> findAllById(@Param("id") Long id);
}
