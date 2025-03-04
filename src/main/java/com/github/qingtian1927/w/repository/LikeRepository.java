package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.Like;
import com.github.qingtian1927.w.model.LikeId;
import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, LikeId> {
    List<Like> findAllByUser(User user);
    int countByPost(Post post);
}
