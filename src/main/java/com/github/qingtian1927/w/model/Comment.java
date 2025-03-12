package com.github.qingtian1927.w.model;

import com.github.qingtian1927.w.utils.ContentFormatter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment implements Searchable {
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

    @Column(name = "content", nullable = false, length = 1024, columnDefinition = "NVARCHAR(MAX)")
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

    public Comment(Long id, String content, Date createdDate, Date updatedDate, Post post, Comment replyTo, User user) {
        this.id = id;
        this.content = content;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.post = post;
        this.replyTo = replyTo;
        this.user = user;
    }

    public String getContent() {
        return ContentFormatter.formatLineBreak(this.content);
    }

    @Override
    public String getType() {
        return Searchable.TYPE_COMMENT;
    }
}
