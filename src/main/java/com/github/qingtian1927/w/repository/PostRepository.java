package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
    List<Post> findAllByOrderByCreatedDateAsc();
    List<Post> findAllByOrderByCreatedDateDesc();
    List<Post> findByRepost(Post post);
    int countByRepost(Post post);
    List<Post> findTop10ByOrderByCreatedDateDesc();
}
