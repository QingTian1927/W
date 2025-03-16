package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Optional<Post> findById(Long id);
    List<Post> findByRepost(Post post);
    List<Post> findByUser(User user);
    List<Post> findAllByOrderByCreatedDateDesc();
    List<Post> findAllByOrderByCreatedDateAsc();
    List<Post> listRecentPosts();
    List<Post> findAll();
    Post save(Post post);
    int countByRepost(Post post);
    long count();
    Page<Post> findAll(Pageable pageable);
    void deleteById(Long id);
}
