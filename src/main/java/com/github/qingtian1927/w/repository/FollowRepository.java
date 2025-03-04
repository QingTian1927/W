package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.Follow;
import com.github.qingtian1927.w.model.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    List<Follow> findByFollowerId(Long followerId);
    List<Follow> findByFollowedId(Long followedId);
    int countByFollowerId(Long followerId);
    int countByFollowedId(Long followedId);
}
