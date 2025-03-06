package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.Notification;
import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.model.dto.NotificationForm;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    List<Notification> findByUser(User user);

    Optional<Notification> findById(Long id);

    Notification save(Notification notification);

    void deleteById(Long id);

    static Notification buildNotification(NotificationForm params, User toUser, Optional<User> fromUser, Optional<Post> referencedPost) {
        Notification notification = new Notification();
        notification.setToUser(toUser);

        switch (params.getType().toLowerCase()) {
            case Notification.ADMIN -> {
                if (fromUser.isEmpty()) {
                    throw new IllegalArgumentException("Sender user not found");
                }

                notification.setType(params.getType());
                notification.setFromUser(fromUser.get());
                notification.setContent(params.getContent());
            }
            case Notification.FOLLOW -> {
                if (fromUser.isEmpty()) {
                    throw new IllegalArgumentException("Sender user not found");
                }

                notification.setType(params.getType());
                notification.setFromUser(fromUser.get());
            }
            case Notification.COMMENT, Notification.LIKE, Notification.REPOST -> {
                if (referencedPost.isEmpty()) {
                    throw new IllegalArgumentException("Referenced post not found");
                }

                if (fromUser.isEmpty()) {
                    throw new IllegalArgumentException("Sender user not found");
                }

                notification.setType(params.getType());
                notification.setFromUser(fromUser.get());
                notification.setReferencedPost(referencedPost.get());
            }
            default -> {
                notification.setType(Notification.GENERAL);
                notification.setContent(params.getContent());
            }
        }

        return notification;
    }
}
