package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.*;
import com.github.qingtian1927.w.model.dto.SearchQuery;
import com.github.qingtian1927.w.service.interfaces.CommentService;
import com.github.qingtian1927.w.service.interfaces.PostService;
import com.github.qingtian1927.w.service.interfaces.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
@Repository
public class SearchRepository {
    public static final int POST_PAGE_SIZE = 30;
    public static final int COMMENT_PAGE_SIZE = 50;
    public static final int PROFILE_PAGE_SIZE = 15;

    public static final String TABLE_POSTS = "posts";
    public static final String TABLE_COMMENTS = "comments";
    public static final String TABLE_PROFILES = "profiles";

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public SearchRepository(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    private static boolean isInvalidPageNumber(Integer pageNumber) {
        return pageNumber < 0;
    }

    private Post mapToPost(Object[] object) {
        if (object[4] == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        Optional<User> user = userService.findById(((Number) object[4]).longValue());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Optional<Post> repost = Optional.empty();
        if (object[3] != null) {
            repost = postService.findById(((Number) object[3]).longValue());
        }

        return repost.map(post -> new Post(
                ((Number) object[0]).longValue(),
                (String) object[1],
                (Date) object[2],
                post,
                user.get()
        )).orElseGet(() -> new Post(
                ((Number) object[0]).longValue(),
                (String) object[1],
                (Date) object[2],
                null,
                user.get()
        ));
    }

    private Profile mapToProfile(Object[] object) {
        Optional<User> user = userService.findById(((Number) object[0]).longValue());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return new Profile(
                (String) object[3],
                (byte[]) object[1],
                (byte[]) object[2],
                user.get()
        );
    }

    private Comment mapToComment(Object[] object) {
        if (object[4] == null) {
            throw new IllegalArgumentException("Post ID cannot be null");
        }

        if (object[6] == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        Optional<User> user = userService.findById(((Number) object[6]).longValue());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Optional<Post> post = postService.findById(((Number) object[4]).longValue());
        if (post.isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }

        Optional<Comment> reply = Optional.empty();
        if (object[5] != null) {
            reply = commentService.findById(((Number) object[5]).longValue());
        }

        return reply.map(comment -> new Comment(
                ((Number) object[0]).longValue(),
                (String) object[1],
                (Date) object[2],
                (Date) object[3],
                post.get(),
                comment,
                user.get()
        )).orElseGet(() -> new Comment(
                ((Number) object[0]).longValue(),
                (String) object[1],
                (Date) object[2],
                (Date) object[3],
                post.get(),
                null,
                user.get()
        ));
    }

    private Query buildQuery(SearchQuery searchQuery, String table) {
        String terms = searchQuery.getQuery();
        Integer pageNumber = searchQuery.getPage();
        String sort = searchQuery.getSort();

        String sql;
        if (sort == null) {
            switch (table) {
                case TABLE_POSTS, TABLE_COMMENTS -> sql = "SELECT * FROM %s WHERE FREETEXT(content, :terms)";
                case TABLE_PROFILES -> sql = "SELECT * FROM %s WHERE FREETEXT(bio, :terms)";
                default -> throw new IllegalArgumentException("Invalid table: " + table);
            }
        } else {
            boolean isDesc = sort.contains("desc");

            switch (table) {
                case TABLE_POSTS, TABLE_COMMENTS -> sql = "SELECT * FROM %s WHERE FREETEXT(content, :terms) ORDER BY created_at";
                case TABLE_PROFILES -> sql = "SELECT * FROM %s WHERE FREETEXT(bio, :terms) ORDER BY user_id";
                default -> throw new IllegalArgumentException("Invalid table: " + table);
            }

            sql += " ";
            sql += (isDesc) ? "DESC" : "ASC";
        }

        sql = String.format(sql, table);
        int pageSize = 0;

        if (pageNumber != null) {
            sql += "OFFSET :rows ROWS FETCH NEXT :pageSize ROWS ONLY";

            switch (table) {
                case TABLE_POSTS -> pageSize = POST_PAGE_SIZE;
                case TABLE_COMMENTS -> pageSize = COMMENT_PAGE_SIZE;
                case TABLE_PROFILES -> pageSize = PROFILE_PAGE_SIZE;
                default -> throw new IllegalArgumentException("Invalid table: " + table);
            }
        }

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("terms", terms);

        if (pageNumber != null) {
            if (isInvalidPageNumber(pageNumber)) {
                throw new IllegalArgumentException("Invalid page number: " + pageNumber);
            }
            query.setParameter("rows", pageNumber * pageSize);
            query.setParameter("pageSize", pageSize);
        }

        System.out.println("SQL: " + sql);
        return query;
    }

    public List<Post> searchPosts(SearchQuery searchQuery) {
        Query query = buildQuery(searchQuery, TABLE_POSTS);
        List<Object[]> results = (List<Object[]>) query.getResultList();
        List<Post> posts = new ArrayList<>();

        for (Object[] result : results) {
            posts.add(mapToPost(result));
        }
        return posts;
    }

    public List<Comment> searchComments(SearchQuery searchQuery) {
        Query query = buildQuery(searchQuery, TABLE_COMMENTS);
        List<Object[]> results = (List<Object[]>) query.getResultList();
        List<Comment> comments = new ArrayList<>();

        for (Object[] result : results) {
            comments.add(mapToComment(result));
        }
        return comments;
    }

    public List<Profile> searchProfiles(SearchQuery searchQuery) {
        Query query = buildQuery(searchQuery, TABLE_PROFILES);
        List<Object[]> results = (List<Object[]>) query.getResultList();
        List<Profile> profiles = new ArrayList<>();

        for (Object[] result : results) {
            profiles.add(mapToProfile(result));
        }
        return profiles;
    }

    public List<Searchable> searchAll(SearchQuery searchQuery) {
        List<Searchable> resultList = new ArrayList<>();

        SearchQuery searchAllQuery = new SearchQuery(searchQuery.getQuery(), null, searchQuery.getPage(), searchQuery.getSort());
        resultList.addAll(this.searchPosts(searchAllQuery));
        resultList.addAll(this.searchComments(searchAllQuery));
        resultList.addAll(this.searchProfiles(searchAllQuery));

        return resultList;
    }

    public List<Searchable> search(SearchQuery searchQuery) {
        List<Searchable> results = new ArrayList<>();

        String filter = searchQuery.getFilter();
        if (filter == null || filter.isEmpty()) {
            return searchAll(searchQuery);
        }

        switch (filter) {
            case SearchQuery.FILTER_POSTS -> results.addAll(this.searchPosts(searchQuery));
            case SearchQuery.FILTER_COMMENTS -> results.addAll(this.searchComments(searchQuery));
            case SearchQuery.FILTER_USERS -> results.addAll(this.searchProfiles(searchQuery));
        }

        return results;
    }
}
