package com.example.groupProject.service.memo;

import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.memo.SkincareDto;

import java.util.List;

public interface SkincareService {

    Long saveSkincareMemo(SkincareDto skincareDto, User user);

    Skincare findById(Long id);

    void deleteByIdSkincareMemo(Long id);

    void updateSkincareMemo(Long id, SkincareDto skincareDto);

    List<Skincare> findAllSkincareMemo();
}
