package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Optional<Post> findById(Long id);
    List<Post> findByRepost(Post post);
    List<Post> findByUser(User user);
    List<Post> findAllByOrderByCreatedDateDesc();
    List<Post> findAllByOrderByCreatedDateAsc();
    List<Post> listRecentPosts();
    Post save(Post post);
    int countByRepost(Post post);
    long count();
}
