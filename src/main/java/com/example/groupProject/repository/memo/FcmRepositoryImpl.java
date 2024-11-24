package com.example.groupProject.repository.memo;

import com.example.groupProject.domain.memo.Memo;
import com.example.groupProject.domain.user.DeviceToken;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FcmRepositoryImpl implements FcmRepository {

    private final EntityManager em;

    @Override
    public void save(DeviceToken deviceToken) {
        if(deviceToken.getId() == null) {
            em.persist(deviceToken);
        }
        else {
            em.merge(deviceToken);
        }
    }

    @Override
    public List<Memo> findByEndDateAfter() {
        LocalDate today = LocalDate.now();
        return em.createQuery("select m from Memo m where m.end_date < :today", Memo.class)
                .setParameter("today", today)
                .getResultList();
    }

    @Override
    public List<String> findDeviceTokensByMemo(Long userId) {
        return em.createQuery(
                "select dt.deviceNumber " +
                "from DeviceToken dt " +
                "join dt.user u " +
                "where u.id = :userId", String.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
