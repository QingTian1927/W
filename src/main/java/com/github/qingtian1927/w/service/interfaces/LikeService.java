package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.Like;
import com.github.qingtian1927.w.model.LikeId;
import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;

import java.util.List;
import java.util.Optional;

public interface LikeService {
    void deleteById(User user, Post post);
    List<Like> findAllByUser(User user);
    Optional<Like> findById(LikeId likeId);
    Optional<Like> findById(User user, Post post);
    Like save(Like like);
    int countByPost(Post post);
}
