package com.example.groupProject.service.memo;

import com.example.groupProject.domain.Memo.Skincare;

public interface SkincareService {

    public Long saveSkincareMemo(Skincare skincare);

    public Skincare findById(Long id);
}
