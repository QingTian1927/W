package com.github.qingtian1927.w.model.dto;

import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.utils.ContentFormatter;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SimilarPost {
    public Post post;
    public User user;
    public List<String> topics;

    public String getContent() {
        return ContentFormatter.getShortenedPreview(this.post.getContent());
    }
}
