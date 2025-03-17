package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
    List<Post> findAllByOrderByCreatedDateAsc();
    Page<Post> findAllByOrderByCreatedDateDesc(Pageable pageable);
    List<Post> findByRepost(Post post);
    int countByRepost(Post post);
    List<Post> findTop10ByOrderByCreatedDateDesc();

    @Query("SELECT p FROM Post p JOIN Follow f ON p.user = f.followed AND f.follower = :follower OR p.user = :follower")
    Page<Post> findAllFromFollowed(Pageable pageable, @Param("follower") User follower);
}
