package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.repository.PostRepository;
import com.github.qingtian1927.w.service.interfaces.CommentService;
import com.github.qingtian1927.w.service.interfaces.LikeService;
import com.github.qingtian1927.w.service.interfaces.NotificationService;
import com.github.qingtian1927.w.service.interfaces.PostService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CommentService commentService;
    private final LikeService likeService;
    private final NotificationService notificationService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, CommentService commentService, LikeService likeService, NotificationService notificationService) {
        this.postRepository = postRepository;
        this.commentService = commentService;
        this.likeService = likeService;
        this.notificationService = notificationService;
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
    public Page<Post> findAllByOrderByCreatedDateDesc(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedDateDesc(pageable);
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
    public List<Post> findAll() {
        return postRepository.findAll();
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

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            notificationService.deleteByReferencedPost(post.get());
            likeService.deleteByPost(post.get());
            commentService.deleteByPost(post.get());
            postRepository.deleteById(id);
        }
    }
}
