package com.example.groupProject.service.memo;

import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.memo.SkincareDto;
import com.example.groupProject.dto.memo.SkincarePageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SkincareService {

    Long saveSkincareMemo(SkincareDto skincareDto, User user);

    SkincareDto findById(Long id);

    void deleteByIdSkincareMemo(Long id);

    void updateSkincareMemo(Long id, SkincareDto skincareDto);

    Page<SkincareDto> findAllSkincareMemoPagingByUserId(SkincarePageDto skincarePageDto);

    void trashSkincareMemo(Long id);

    void recoverSkincareMemo(Long id);
}
