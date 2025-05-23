package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.Comment;
import com.github.qingtian1927.w.model.CommentLike;
import com.github.qingtian1927.w.model.CommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
    int countByComment(Comment comment);
    void deleteByComment(Comment comment);
}
