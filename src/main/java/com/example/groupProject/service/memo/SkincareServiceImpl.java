package com.example.groupProject.service.memo;

import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.repository.memo.SkincareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkincareServiceImpl implements SkincareService {

    private final SkincareRepository skincareRepository;

    @Override
    @Transactional
    public Long saveSkincareMemo(Skincare skincare) {
        skincareRepository.save(skincare);
        return skincare.getId();
    }

    @Override
    public Skincare findById(Long id) {
        return skincareRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteSkincareMemo(Skincare skincare) {
        skincareRepository.delete(skincare);
    }


}
