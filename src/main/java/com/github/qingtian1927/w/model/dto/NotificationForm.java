package com.github.qingtian1927.w.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class NotificationForm implements Serializable {
    private String type;
    private String content;
    private Long referencedPostId;
}
