package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.Like;
import com.github.qingtian1927.w.model.LikeId;
import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.repository.LikeRepository;
import com.github.qingtian1927.w.service.interfaces.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;

    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public void deleteById(User user, Post post) {
        this.likeRepository.deleteById(new LikeId(user.getId(), post.getId()));
    }

    @Override
    public List<Like> findAllByUser(User user) {
        return this.likeRepository.findAllByUser(user);
    }

    @Override
    public Optional<Like> findById(LikeId likeId) {
        return this.likeRepository.findById(likeId);
    }

    @Override
    public Optional<Like> findById(User user, Post post) {
        return this.findById(new LikeId(user.getId(), post.getId()));
    }

    @Override
    public Like save(Like like) {
        return this.likeRepository.save(like);
    }

    @Override
    public int countByPost(Post post) {
        return this.likeRepository.countByPost(post);
    }

    @Override
    public void deleteByPost(Post post) {
        this.likeRepository.deleteByPost(post);
    }
}
