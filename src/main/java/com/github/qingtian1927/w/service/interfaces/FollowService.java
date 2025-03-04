package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.Follow;
import com.github.qingtian1927.w.model.User;

import java.util.List;
import java.util.Optional;

public interface FollowService {
    List<Follow> findByFollowerId(Long followerId);
    List<Follow> findByFollowedId(Long followedId);
    Optional<Follow> findById(User followerId, User followedId);
    int countByFollowerId(Long followerId);
    int countByFollowedId(Long followedId);
    Follow save(User follower, User followed);
    void deleteById(User follower, User followed);
}
