package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.*;
import com.github.qingtian1927.w.repository.CommentLikeRepository;
import com.github.qingtian1927.w.repository.CommentRepository;
import com.github.qingtian1927.w.service.interfaces.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentLikeRepository commentLikeRepository) {
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> findByUser(User user) {
        return this.commentRepository.findByUser(user);
    }

    @Override
    public List<Comment> listPostCommentDateAsc(Post post) {
        return this.commentRepository.findCommentByPostAndReplyToIsNullOrderByCreatedDateAsc(post);
    }

    @Override
    public List<Comment> listPostCommentDateDesc(Post post) {
        return this.commentRepository.findCommentByPostAndReplyToIsNullOrderByCreatedDateDesc(post);
    }

    @Override
    public List<Comment> listCommentReplyDateDesc(Comment comment) {
        return this.commentRepository.findCommentByReplyToOrderByCreatedDateDesc(comment);
    }

    @Override
    public List<Comment> listCommentReplyDateAsc(Comment comment) {
        return this.commentRepository.findCommentByReplyToOrderByCreatedDateAsc(comment);
    }

    @Override
    public Optional<CommentLike> findCommentLikeById(User user, Comment comment) {
        return this.commentLikeRepository.findById(new CommentLikeId(user.getId(), comment.getId()));
    }

    @Override
    public int countByPost(Post post) {
        return this.commentRepository.countByPost(post);
    }

    @Override
    public int countReplyByComment(Comment comment) {
        return this.commentRepository.countCommentByReplyTo(comment);
    }

    @Override
    public int countLikesByComment(Comment comment) {
        return this.commentLikeRepository.countByComment(comment);
    }

    @Override
    public Comment save(Comment comment) {
        return this.commentRepository.save(comment);
    }

    @Override
    public CommentLike like(User user, Comment comment) {
        return this.commentLikeRepository.save(new CommentLike(user, comment));
    }

    @Override
    public void unlike(User user, Comment comment) {
        this.commentLikeRepository.deleteById(new CommentLikeId(user.getId(), comment.getId()));
    }

    @Override
    public long count() {
        return this.commentRepository.count();
    }
}
