package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.Comment;
import com.github.qingtian1927.w.model.CommentLike;
import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<Comment> findById(Long id);
    List<Comment> findByUser(User user);
    List<Comment> listPostCommentDateAsc(Post post);
    List<Comment> listPostCommentDateDesc(Post post);
    List<Comment> listCommentReplyDateDesc(Comment comment);
    List<Comment> listCommentReplyDateAsc(Comment comment);
    Optional<CommentLike> findCommentLikeById(User user, Comment comment);
    int countByPost(Post post);
    int countReplyByComment(Comment comment);
    int countLikesByComment(Comment comment);
    Comment save(Comment comment);
    CommentLike like(User user, Comment comment);
    void unlike(User user, Comment comment);
    long count();
}
