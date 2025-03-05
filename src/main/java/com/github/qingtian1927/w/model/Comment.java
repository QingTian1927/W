package com.github.qingtian1927.w.model;

import com.github.qingtian1927.w.utils.ContentFormatter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @ToString.Exclude
    private Post post;

    @Column(name = "content", nullable = false, length = 300, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reply_to_id")
    @ToString.Exclude
    private Comment replyTo;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Date updatedDate = this.createdDate;

    public Comment(User user, Post post, String content) {
        this.user = user;
        this.post = post;
        this.content = content;
    }

    public Comment(User user, Post post, String content, Comment replyTo) {
        this.user = user;
        this.post = post;
        this.content = content;
        this.replyTo = replyTo;
    }

    public String getContent() {
        return ContentFormatter.formatLineBreak(this.content);
    }
}
