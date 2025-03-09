package com.github.qingtian1927.w.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class UserGrowthCount {
    private Date date;
    private int count;
}
