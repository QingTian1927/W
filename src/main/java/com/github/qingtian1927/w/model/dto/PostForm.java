package com.github.qingtian1927.w.model.dto;

import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class PostForm implements Serializable {
    @Size(min = 1, max = 1024)
    private String content;

    public Post toPost(User user) {
        return new Post(this.content, user);
    }
}
