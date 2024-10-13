package com.example.groupProject.repository.memo;

import com.example.groupProject.domain.Memo.Skincare;

public interface SkincareRepository {

    //기초제품 메모 추가
    public void save(Skincare skincare);

    public Skincare findById(Long id);

    public void delete(Skincare skincare);
}
