package com.github.qingtian1927.w.model.dto;

import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.StringJoiner;

@Data
@AllArgsConstructor
public class TrendingPost {
    public static final int PREVIEW_SIZE = 20;

    private Post post;
    private User user;
    private List<String> topics;
    private int likes;
    private int comments;
    private int trendingScore;

    public String getPreview() {
        String[] words = post.getContent().split(" ");
        StringJoiner sj = new StringJoiner(" ");
        int wordCount = 0;

        for (String word : words) {
            if (wordCount <= PREVIEW_SIZE) {
                sj.add(word);
                wordCount++;
            }
        }

        return sj.toString().concat("...");
    }
}
