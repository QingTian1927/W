package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.Follow;
import com.github.qingtian1927.w.model.FollowId;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.repository.FollowRepository;
import com.github.qingtian1927.w.service.interfaces.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;

    @Autowired
    public FollowServiceImpl(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public List<Follow> findByFollowerId(Long followerId) {
        return followRepository.findByFollowerId(followerId);
    }

    @Override
    public List<Follow> findByFollowedId(Long followedId) {
        return followRepository.findByFollowedId(followedId);
    }

    @Override
    public Optional<Follow> findById(User follower, User followed) {
        return followRepository.findById(new FollowId(follower.getId(), followed.getId()));
    }

    @Override
    public int countByFollowerId(Long followerId) {
        return followRepository.countByFollowerId(followerId);
    }

    @Override
    public int countByFollowedId(Long followedId) {
        return followRepository.countByFollowedId(followedId);
    }

    @Override
    public Follow save(User follower, User followed) {
        Follow follow = new Follow(follower, followed);
        System.out.println(follow);
        return followRepository.save(follow);
    }

    @Override
    public void deleteById(User follower, User followed) {
        followRepository.deleteById(new FollowId(follower.getId(), followed.getId()));
    }
}
