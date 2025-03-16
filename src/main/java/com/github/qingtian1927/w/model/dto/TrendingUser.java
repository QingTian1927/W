package com.github.qingtian1927.w.model.dto;

import com.github.qingtian1927.w.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrendingUser {
    private User user;
    private int trendingScore;
}
