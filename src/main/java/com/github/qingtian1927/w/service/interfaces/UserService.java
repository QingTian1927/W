package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    boolean exists(User user);
    boolean exists(String email, String handle);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String username);
    List<User> findAll();
    Page<User> findAll(Pageable pageable);
    User save(User user);
    long count();
    List<User> listRecentUsers();
}
