package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.Notification;
import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByToUserOrderByCreatedDateDesc(User user);
    void deleteByReferencedPost(Post referencedPost);
}
