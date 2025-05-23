package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.Notification;
import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.model.dto.NotificationForm;
import com.github.qingtian1927.w.repository.NotificationRepository;
import com.github.qingtian1927.w.service.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> findByUser(User user) {
        return notificationRepository.findByToUserOrderByCreatedDateDesc(user);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public int countUnreadNotification(User user) {
        return this.notificationRepository.countByToUserAndIsReadFalse(user);
    }

    @Override
    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public void deleteByReferencedPost(Post post) {
        notificationRepository.deleteByReferencedPost(post);
    }
}
