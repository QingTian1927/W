package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailAndUsername(String email, String accountHandle);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> getByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findTop10ByOrderByCreatedDateDesc();
}
