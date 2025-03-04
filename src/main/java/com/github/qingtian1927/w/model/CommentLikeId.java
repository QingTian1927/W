package com.github.qingtian1927.w.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "comment_id")
    private Long commentId;
}
