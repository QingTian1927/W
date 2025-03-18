package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.Bookmark;
import com.github.qingtian1927.w.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    List<Bookmark> findByUser(User user);
    Optional<Bookmark> findByUserAndTypeAndReferenceId(User user, String type, Long referenceId);
    boolean existsByUserAndTypeAndReferenceId(User user, String type, Long referenceId);
}
