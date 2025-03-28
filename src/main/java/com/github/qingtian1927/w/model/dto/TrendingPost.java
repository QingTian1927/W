package com.github.qingtian1927.w.model.dto;

import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.utils.ContentFormatter;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TrendingPost {
    private Post post;
    private User user;
    private List<String> topics;
    private int likes;
    private int comments;
    private int trendingScore;

    public String getPreview() {
        return ContentFormatter.getShortenedPreview(this.post.getContent());
    }
}
