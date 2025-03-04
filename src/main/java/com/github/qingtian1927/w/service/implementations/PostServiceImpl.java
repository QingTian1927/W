package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.repository.PostRepository;
import com.github.qingtian1927.w.service.interfaces.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> findByRepost(Post post) {
        return postRepository.findByRepost(post);
    }

    @Override
    public List<Post> findByUser(User user) {
        return postRepository.findByUser(user);
    }

    @Override
    public List<Post> findAllByOrderByCreatedDateDesc() {
        return postRepository.findAllByOrderByCreatedDateDesc();
    }

    @Override
    public List<Post> findAllByOrderByCreatedDateAsc() {
        return postRepository.findAllByOrderByCreatedDateAsc();
    }

    @Override
    public List<Post> listRecentPosts() {
        return postRepository.findTop10ByOrderByCreatedDateDesc();
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public int countByRepost(Post post) {
        return postRepository.countByRepost(post);
    }

    @Override
    public long count() {
        return postRepository.count();
    }
}
