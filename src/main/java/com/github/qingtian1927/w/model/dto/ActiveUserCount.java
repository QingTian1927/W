package com.github.qingtian1927.w.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActiveUserCount {
    private int thisWeek;
    private int lastWeek;
}
