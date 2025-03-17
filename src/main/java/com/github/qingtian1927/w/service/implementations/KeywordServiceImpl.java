package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.Keyword;
import com.github.qingtian1927.w.model.Post;
import com.github.qingtian1927.w.model.dto.SimilarPost;
import com.github.qingtian1927.w.repository.KeywordRepository;
import com.github.qingtian1927.w.service.interfaces.KeywordService;
import com.github.qingtian1927.w.service.interfaces.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KeywordServiceImpl implements KeywordService {
    private final KeywordRepository keywordRepository;
    private final PostService postService;

    @Autowired
    public KeywordServiceImpl(KeywordRepository keywordRepository, PostService postService) {
        this.keywordRepository = keywordRepository;
        this.postService = postService;
    }

    public List<String> findMainTopics(String content) {
        List<Keyword> matchedKeywords = keywordRepository.findMatchingKeywords(content.toLowerCase());
        System.out.println(content);
        System.out.println(matchedKeywords);

        // Group the matched keywords by their category and count occurrences
        // Get the top 3 categories sorted by count in descending order

        Map<String, Long> categoryCounts = matchedKeywords.stream()
                .collect(Collectors.groupingBy(Keyword::getCategory, Collectors.counting()));

        return categoryCounts.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue())) // Sort by count descending
                .limit(MAIN_TOPIC_SIZE)
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<SimilarPost> findSimilarPosts(Post originalPost) {
        List<String> topics = originalPost.getTopics();

        if (topics.isEmpty()) {
            return Collections.emptyList();
        }

        List<Post> allPosts = postService.findAll();

        // Filter posts that share at least one topic
        // Check for topic overlap
        // Rank by topic match count
        // Limit results to top 10 similar posts

        return allPosts.stream()
                .filter(post -> !post.getId().equals(originalPost.getId()))
                .map(post -> {
                    List<String> postTopics = post.getTopics();
                    return new SimilarPost(post, post.getUser(), postTopics);
                })
                .filter(similarPost -> !Collections.disjoint(topics, similarPost.getTopics())
                )
                .sorted(Comparator.comparingInt(similarPost ->
                        (int) topics.stream().filter(((SimilarPost) similarPost).getTopics()::contains).count()
                ).reversed())
                .limit(SIMILAR_POST_SIZE)
                .toList();
    }
}
