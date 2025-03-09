package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.dto.ActiveUserCount;
import com.github.qingtian1927.w.model.dto.AgeGroupCount;
import com.github.qingtian1927.w.model.dto.PostCount;
import com.github.qingtian1927.w.model.dto.UserGrowthCount;
import com.github.qingtian1927.w.repository.StatisticsRepository;
import com.github.qingtian1927.w.service.interfaces.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsRepository statisticsRepository;

    @Autowired
    public StatisticsServiceImpl(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
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
    public PostCount countNewPost() {
        return statisticsRepository.countNewPosts();
    }

    @Override
    public ActiveUserCount countActiveUsers() {
        return statisticsRepository.countActiveUsers();
    }
}
