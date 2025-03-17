package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.dto.SimilarPost;

import java.util.List;

public interface KeywordService {
    int MAIN_TOPIC_SIZE = 3;
    int SIMILAR_POST_SIZE = 3;

    List<String> findMainTopics(String content);
    List<SimilarPost> findSimilarPosts(Post originalPost);
}
