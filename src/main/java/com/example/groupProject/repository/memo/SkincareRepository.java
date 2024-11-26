package com.example.groupProject.repository.memo;

import com.example.groupProject.domain.memo.Skincare;

public interface SkincareRepository {
    void save(Skincare skincare);

    Skincare findById(Long id);

    void delete(Skincare skincare);

    void deleteById(Long id);
}
