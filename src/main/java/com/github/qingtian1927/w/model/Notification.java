package com.github.qingtian1927.w.model;

import com.github.qingtian1927.w.utils.ContentFormatter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
@Table(name = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    public static final String ADMIN = "admin";
    public static final String LIKE = "like";
    public static final String REPOST = "repost";
    public static final String COMMENT = "comment";
    public static final String FOLLOW = "follow";
    public static final String GENERAL = "general";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type", length = 64, nullable = false)
    private String type = GENERAL;

    @Column(name = "content", length = 1024, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User fromUser; ;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    @ManyToOne
    @JoinColumn(name = "referenced_post_id")
    private Post referencedPost;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = Boolean.FALSE;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    public Notification(String type, String content, User toUser, User fromUser, Post referencedPost) {
        this.type = type;
        this.content = content;
        this.toUser = toUser;
        this.fromUser = fromUser;
        this.referencedPost = referencedPost;
    }

    public Notification(String type, String content, User toUser) {
        this.type = type;
        this.content = content;
        this.toUser = toUser;
    }

    public Notification(String content, User toUser) {
        this.content = content;
        this.toUser = toUser;
    }

    public void setType(String type) {
        this.type = type.toLowerCase();
    }

    public String getContent() {
        return ContentFormatter.formatLineBreak(this.content);
    }
}
