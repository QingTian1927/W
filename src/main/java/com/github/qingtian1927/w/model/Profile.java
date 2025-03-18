package com.github.qingtian1927.w.model;

import com.github.qingtian1927.w.utils.ContentFormatter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
public class Profile implements Searchable {
    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "bio", length = 160, columnDefinition = "NVARCHAR(160)")
    private String bio;

    @Lob
    @Column(name = "avatar", columnDefinition = "VARBINARY(MAX)")
    @ToString.Exclude
    private byte[] avatar;

    @Lob
    @Column(name = "banner", columnDefinition = "VARBINARY(MAX)")
    @ToString.Exclude
    private byte[] banner;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Profile(String bio, byte[] avatar, byte[] banner, User user) {
        this.bio = bio;
        this.avatar = avatar;
        this.banner = banner;
        this.user = user;
        this.id = user.getId();
    }

    public Profile(User user) {
        this.user = user;
        this.id = user.getId();
    }

    public void setAvatar(MultipartFile avatar) throws IOException {
        this.avatar = avatar.getBytes();
    }

    public void setBanner(MultipartFile banner) throws IOException {
        this.banner = banner.getBytes();
    }

    public String getBio() {
        return ContentFormatter.formatLineBreak(this.bio);
    }

    @Override
    public String getType() {
        return Searchable.TYPE_PROFILE;
    }

    @Override
    public String getContent() {
        return this.bio;
    }

    @Override
    public void setContent(String content) {
        this.bio = content;
    }
}
