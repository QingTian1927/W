package com.github.qingtian1927.w.model;

public interface Searchable {
    String TYPE_POST = "post";
    String TYPE_COMMENT = "comment";
    String TYPE_PROFILE = "profile";

    String getType();
    String getContent();
    void setContent(String content);
}
