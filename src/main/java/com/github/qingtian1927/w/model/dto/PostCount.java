package com.github.qingtian1927.w.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostCount {
    private int today;
    private int thisWeek;
    private int thisMonth;

    private int yesterday;
    private int lastWeek;
    private int lastMonth;
}
