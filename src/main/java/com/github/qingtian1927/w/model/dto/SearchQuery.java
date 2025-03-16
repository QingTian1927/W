package com.github.qingtian1927.w.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.io.Serializable;

@Data
public class SearchQuery implements Serializable {
    public static final String FILTER_POSTS = "posts";
    public static final String FILTER_COMMENTS = "comments";
    public static final String FILTER_USERS = "users";

    public static final String SORT_DATE_ASC = "date-asc";
    public static final String SORT_DATE_DESC = "date-desc";

    @NotNull
    private String query;
    @Null
    private String filter = null;
    @Null
    private Integer page = null;
    @Null
    private String sort = SORT_DATE_ASC;

    public SearchQuery(String query, String filter, Integer page, String sort) {
        this.query = query;
        this.setFilter(filter);
        this.setPage(page);
        this.setSort(sort);
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

    public void setSort(String sort) {
        if (sort == null || sort.isEmpty()) {
            this.sort = sort;
            return;
        }

        if (!sort.equals(SORT_DATE_ASC) && !sort.equals(SORT_DATE_DESC)) {
            throw new IllegalArgumentException("Invalid sort: " + sort);
        }
        this.sort = sort;
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
