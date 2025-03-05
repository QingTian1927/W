package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.Notification;
import com.github.qingtian1927.w.model.User;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    List<Notification> findByUser(User user);
    Optional<Notification> findById(Long id);
    Notification save(Notification notification);
    void deleteById(Long id);
}
