package com.example.groupProject.repository.user;

import com.example.groupProject.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.account = :account")
    List<User> findByAccount(@Param(value = "account") String account);
}
