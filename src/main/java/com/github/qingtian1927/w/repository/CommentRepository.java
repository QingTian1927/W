package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.Comment;
import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUser(User user);
    List<Comment> findByReplyTo(Comment replyTo);
    List<Comment> findByPost(Post post);
    List<Comment> findCommentByPostAndReplyToIsNullOrderByCreatedDateDesc(Post post);
    List<Comment> findCommentByPostAndReplyToIsNullOrderByCreatedDateAsc(Post post);
    List<Comment> findCommentByReplyToOrderByCreatedDateDesc(Comment replyTo);
    List<Comment> findCommentByReplyToOrderByCreatedDateAsc(Comment replyTo);
    int countByPost(Post post);
    int countCommentByReplyTo(Comment comment);
    void deleteByPost(Post post);
    void deleteByReplyTo(Comment replyTo);
}
