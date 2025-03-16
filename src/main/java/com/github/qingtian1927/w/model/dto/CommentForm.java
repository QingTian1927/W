package com.github.qingtian1927.w.model.dto;

import com.github.qingtian1927.w.model.Comment;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class CommentForm implements Serializable {
    @Size(min = 1, max = 1024)
    private String content;
    private Long replyToId;
}
