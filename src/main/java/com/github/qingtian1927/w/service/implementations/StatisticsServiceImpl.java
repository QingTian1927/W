package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.dto.*;
import com.github.qingtian1927.w.repository.StatisticsRepository;
import com.github.qingtian1927.w.service.interfaces.KeywordService;
import com.github.qingtian1927.w.service.interfaces.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsRepository statisticsRepository;
    private final KeywordService keywordService;

    @Autowired
    public StatisticsServiceImpl(StatisticsRepository statisticsRepository, KeywordService keywordService) {
        this.statisticsRepository = statisticsRepository;
        this.keywordService = keywordService;
    }

    @Override
    public List<AgeGroupCount> countUserByAgeGroup() {
        return statisticsRepository.getUserAgeGroupCount();
    }

    @Override
    public List<UserGrowthCount> countNewUser() {
        return statisticsRepository.countNewUsers();
    }

    @Override
    public PostCount countNewPosts() {
        return statisticsRepository.countNewPosts();
    }

    @Override
    public CommentCount countNewComments() {
        return statisticsRepository.countNewComments();
    }

    @Override
    public ActiveUserCount countActiveUsers() {
        return statisticsRepository.countActiveUsers();
    }

    @Override
    public List<UserActivityCount> countUserActivities() {
        return statisticsRepository.countUserActivities();
    }

    @Override
    public int countBannedUsers() {
        return statisticsRepository.countBannedUsers();
    }

    @Override
    public List<TrendingPost> getWeeklyTrendingPosts(int limit) {
        List<TrendingPost> trendingPosts = statisticsRepository.getWeeklyTrendingPosts(limit);
        for (TrendingPost trendingPost : trendingPosts) {
            trendingPost.setTopics(keywordService.findMainTopics(trendingPost.getPost().getContent()));
        }
        return trendingPosts;
    }
}
