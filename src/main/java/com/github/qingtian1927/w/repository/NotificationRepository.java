package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.Notification;
import com.github.qingtian1927.w.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByToUser(User user);
}
