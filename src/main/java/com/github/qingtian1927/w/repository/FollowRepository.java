package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.Follow;
import com.github.qingtian1927.w.model.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    List<Follow> findByFollowerId(Long followerId);
    List<Follow> findByFollowedId(Long followedId);
    int countByFollowerId(Long followerId);
    int countByFollowedId(Long followedId);
}
