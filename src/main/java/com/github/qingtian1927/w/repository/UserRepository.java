package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailAndUsername(String email, String accountHandle);
    Optional<User> getByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findTop10ByOrderByCreatedDateDesc();
}
