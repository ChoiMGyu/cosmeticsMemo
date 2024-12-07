package com.example.groupProject.repository.memo;

import com.example.groupProject.domain.memo.Skincare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SkincareRepository extends JpaRepository<Skincare, Long> {

    @Query("SELECT s FROM Skincare s WHERE s.master.id = :id ORDER BY s.start_date ASC")
    Page<Skincare> findAllByIdPaging(@Param("id") Long id, Pageable pageable);
}
