package com.github.qingtian1927.w.model;

import com.github.qingtian1927.w.utils.ContentFormatter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
public class Post implements Searchable, Bookmarkable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "content", length = 1024, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "post_topics", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "topic", nullable = false, columnDefinition = "NVARCHAR(128)")
    private List<String> topics = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
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

    public Post(Long id, String content, Date createdDate, Post repost, User user) {
        this.id = id;
        this.content = content;
        this.createdDate = createdDate;
        this.repost = repost;
        this.user = user;
    }

    public String getContent() {
        if (repost == null) {
            return ContentFormatter.formatLineBreak(this.content);
        }

        Post currentPost = this.repost;
        String content = null;

        while (content == null) {
            content = currentPost.getContent();
            currentPost = currentPost.getRepost();
        }

        return ContentFormatter.formatLineBreak(content);
    }

    @Override
    public String getType() {
        return Searchable.TYPE_POST;
    }
}
