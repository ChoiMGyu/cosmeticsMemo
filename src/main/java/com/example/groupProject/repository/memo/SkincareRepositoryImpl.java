package com.example.groupProject.repository.memo;

import com.example.groupProject.domain.memo.Skincare;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SkincareRepositoryImpl implements SkincareRepository{

    private final EntityManager em;

    @Override
    public void save(Skincare skincare) {
        if(skincare.getId() == null) {
            em.persist(skincare);
        }
        else {
            em.merge(skincare);
        }
    }

    @Override
    public Skincare findById(Long id) {
        return em.find(Skincare.class, id);
    }

    @Override
    public void delete(Skincare skincare) {
        em.remove(skincare);
    }
}
