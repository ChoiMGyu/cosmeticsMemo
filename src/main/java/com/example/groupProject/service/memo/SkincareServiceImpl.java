package com.example.groupProject.service.memo;

import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.memo.SkincareDto;
import com.example.groupProject.dto.memo.SkincarePageDto;
import com.example.groupProject.repository.memo.SkincareRepository;
import com.example.groupProject.repository.memo.SkincareSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkincareServiceImpl implements SkincareService {
    private final String NOT_EXIST_MEMO = "선택하신 메모의 ID가 존재하지 않습니다.";

    private final SkincareRepository skincareRepository;

    @Override
    @Transactional
    public Long saveSkincareMemo(SkincareDto skincareDto, User user) {
        Skincare skincare = Skincare.builder()
                .start_date(skincareDto.getStart_date())
                .end_date(skincareDto.getEnd_date())
                .name(skincareDto.getName())
                .description(skincareDto.getDescription())
                .master(user)
                .area(skincareDto.getArea())
                .moisture(skincareDto.getMoisture())
                .build();

        skincareRepository.save(skincare);
        return skincare.getId();
    }

    @Override
    public SkincareDto findById(Long id) {
        Skincare findSkincare = skincareRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_MEMO));
        return SkincareDto.from(findSkincare);
    }

    @Override
    @Transactional
    public void deleteByIdSkincareMemo(Long id) {
        if (!skincareRepository.existsById(id)) {
            throw new IllegalArgumentException(NOT_EXIST_MEMO);
        }
        skincareRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void trashSkincareMemo(Long id) {
        Skincare findSkincare = skincareRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_MEMO));
        findSkincare.moveToTrash();
    }

    @Override
    @Transactional
    public void recoverSkincareMemo(Long id) {
        Skincare findSkincare = skincareRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_MEMO));
        findSkincare.moveToRecover();
    }

    @Override
    @Transactional
    public void updateSkincareMemo(Long id, SkincareDto skincareDto) {
        Skincare findSkincare = skincareRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_MEMO));
        findSkincare.changeSkincare(skincareDto);
    }

    @Override
    public Page<SkincareDto> findAllSkincareMemoPagingByUserId(SkincarePageDto skincarePageDto) {
        Pageable pageable = PageRequest.of(skincarePageDto.getPage(), skincarePageDto.getSize());

        Specification<Skincare> spec = Specification.where(SkincareSpecifications.withMasterId(skincarePageDto.getMasterId()))
                .and(SkincareSpecifications.sortBy(skincarePageDto.getSortBy()))
                .and(SkincareSpecifications.withNotDeleted());

        Page<Skincare> skincareMemoPaging = skincareRepository.findAll(spec, pageable);

        return skincareMemoPaging.map(SkincareDto::from);
    }

}
