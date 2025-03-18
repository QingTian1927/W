package com.github.qingtian1927.w.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "bookmarks")
@Data
@NoArgsConstructor
public class Bookmark {
    public static final String TYPE_POST = "post";
    public static final String TYPE_COMMENT = "comment";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    @Transient
    private Bookmarkable referenceObject = null;

    public static boolean isInvalidType(String type) {
        return !TYPE_POST.equals(type) && !TYPE_COMMENT.equals(type);
    }

    public Bookmark(User user, String type, Long referenceId) {
        this.user = user;
        setType(type);
        this.referenceId = referenceId;
    }

    public void setType(String type) {
        if (isInvalidType(type)) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        this.type = type;
    }
}
