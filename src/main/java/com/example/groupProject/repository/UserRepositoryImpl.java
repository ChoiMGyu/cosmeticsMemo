package com.example.groupProject.repository;

import com.example.groupProject.domain.user.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl {

    private final EntityManager em;


    public void save(User user) {
        em.persist(user);
    }

    public User findOne(Long id) {
        return em.find(User.class, id);
    }

    public List<User> findAll() {
        return em.createQuery("select u from User u", User.class).getResultList();
    }

    public List<User> findByAccount(String account) {
        return em.createQuery("select u from User u where u.account = :account", User.class)
                .setParameter("account", account)
                .getResultList();
    }
}
