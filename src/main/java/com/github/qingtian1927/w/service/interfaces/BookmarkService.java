package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.Bookmark;
import com.github.qingtian1927.w.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookmarkService {
    Bookmark save(Bookmark bookmark);
    Optional<Bookmark> findById(UUID id);
    Optional<Bookmark> findByContent(User user, String type, Long id);
    List<Bookmark> findByUser(User user);
    boolean exists(Bookmark bookmark);
    boolean exists(User user, String type, Long referenceId);
    void deleteById(UUID id);
}
