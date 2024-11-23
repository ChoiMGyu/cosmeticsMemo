package com.example.groupProject.service.memo;

import com.example.groupProject.domain.memo.Skincare;

public interface SkincareService {

    public Long saveSkincareMemo(Skincare skincare);

    public Skincare findById(Long id);

    public void deleteSkincareMemo(Skincare skincare);
}
