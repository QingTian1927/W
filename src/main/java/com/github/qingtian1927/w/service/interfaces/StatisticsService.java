package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.dto.*;

import java.util.List;

public interface StatisticsService {
    List<AgeGroupCount> countUserByAgeGroup();
    List<UserGrowthCount> countNewUser();
    PostCount countNewPosts();
    CommentCount countNewComments();
    ActiveUserCount countActiveUsers();
    List<UserActivityCount> countUserActivities();
    int countBannedUsers();
    List<TrendingPost> getWeeklyTrendingPosts(int limit);
    List<TrendingUser> getWeeklyTrendingUsers(int limit);
}
