package com.github.qingtian1927.w.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgeGroupCount {
    private int age;
    private int count;
    private int percentage;
}
