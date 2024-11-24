package com.example.groupProject.repository.memo;

import com.example.groupProject.domain.memo.Skincare;

public interface SkincareRepository {
    public void save(Skincare skincare);

    public Skincare findById(Long id);

    public void delete(Skincare skincare);
}
