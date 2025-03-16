package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.repository.KeywordRepository;

import java.util.List;

public interface KeywordService {
    int MAIN_TOPIC_SIZE = 3;
    int SIMILAR_POST_SIZE = 10;

    List<String> findMainTopics(String content);
}
