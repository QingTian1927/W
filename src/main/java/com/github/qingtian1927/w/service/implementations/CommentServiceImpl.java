package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.*;
import com.github.qingtian1927.w.repository.CommentLikeRepository;
import com.github.qingtian1927.w.repository.CommentRepository;
import com.github.qingtian1927.w.service.interfaces.CommentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public List<Comment> findByReplyTo(Comment replyTo) {
        return this.commentRepository.findByReplyTo(replyTo);
    }

    @Override
    public List<Comment> findByPost(Post post) {
        return this.commentRepository.findByPost(post);
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

    @Override
    public Page<Comment> findAll(Pageable pageable) {
        return this.commentRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Optional<Comment> comment = this.commentRepository.findById(id);

        if (comment.isPresent()) {
            for (Comment reply : this.findByReplyTo(comment.get())) {
                this.commentLikeRepository.deleteByComment(reply);
                this.commentRepository.deleteById(reply.getId());
            }
            this.commentLikeRepository.deleteByComment(comment.get());
            this.commentRepository.deleteById(comment.get().getId());
        }
    }

    @Override
    @Transactional
    public void deleteByPost(Post post) {
        for (Comment comment : this.commentRepository.findByPost(post)) {
            this.commentLikeRepository.deleteByComment(comment);
            this.commentLikeRepository.flush();

            List<Comment> replies = this.findByReplyTo(comment);
            for (Comment reply : replies) {
                this.commentLikeRepository.deleteByComment(reply);
                this.commentLikeRepository.flush();
                this.commentRepository.deleteById(reply.getId());
            }

            this.commentRepository.deleteById(comment.getId());
        }
    }
}
