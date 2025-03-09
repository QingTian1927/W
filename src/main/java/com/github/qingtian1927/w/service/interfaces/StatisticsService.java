package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.dto.ActiveUserCount;
import com.github.qingtian1927.w.model.dto.AgeGroupCount;
import com.github.qingtian1927.w.model.dto.PostCount;
import com.github.qingtian1927.w.model.dto.UserGrowthCount;

import java.util.List;

public interface StatisticsService {
    List<AgeGroupCount> countUserByAgeGroup();
    List<UserGrowthCount> countNewUser();
    PostCount countNewPost();
    ActiveUserCount countActiveUsers();
}
