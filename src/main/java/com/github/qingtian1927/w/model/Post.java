package com.github.qingtian1927.w.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "content", length = 300, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "repost_source_id")
    private Post repost;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    public Post(String content, User user) {
        this.content = content;
        this.user = user;
    }

    public Post(Post repost, User user) {
        this.repost = repost;
        this.user = user;
    }

    public String getContent() {
        if (repost == null) {
            return this.content;
        }

        Post currentPost = this.repost;
        String content = null;

        while (content == null) {
            content = currentPost.getContent();
            currentPost = currentPost.getRepost();
        }

        return content;
    }
}
