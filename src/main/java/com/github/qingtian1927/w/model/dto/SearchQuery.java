package com.github.qingtian1927.w.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
public class SearchQuery {
    public static final String FILTER_POSTS = "posts";
    public static final String FILTER_COMMENTS = "comments";
    public static final String FILTER_USERS = "users";

    @NotNull
    private String query;
    @Null
    private String filter = null;
    @Null
    private Integer page = null;

    public SearchQuery(String query, String filter, Integer page) {
        this.query = query;
        this.setFilter(filter);
        this.setPage(page);
    }

    public void setFilter(String filter) {
        if (filter == null || filter.isEmpty()) {
            this.filter = filter;
            return;
        }

        if (!filter.equals(FILTER_POSTS) && !filter.equals(FILTER_COMMENTS) && !filter.equals(FILTER_USERS)) {
            throw new IllegalArgumentException("Invalid filter: " + filter);
        }
        this.filter = filter;
    }

    public void setPage(Integer page) {
        if (page == null) {
            this.page = null;
            return;
        }

        if (page - 1 < 0) {
            throw new IllegalArgumentException("Invalid page number: " + page);
        }
        this.page = page;
    }
}
