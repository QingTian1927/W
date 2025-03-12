package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.*;
import com.github.qingtian1927.w.service.interfaces.CommentService;
import com.github.qingtian1927.w.service.interfaces.PostService;
import com.github.qingtian1927.w.service.interfaces.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
@Repository
public class SearchRepository {
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

    public Post mapToPost(Object[] object) {
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

    public Profile mapToProfile(Object[] object) {
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

    public Comment mapToComment(Object[] object) {
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

    public List<Post> searchPosts(String terms) {
        String sql = """
                    SELECT * FROM posts WHERE FREETEXT (content, :terms) ORDER BY created_at
                """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("terms", terms);
        List<Object[]> results = (List<Object[]>) query.getResultList();
        List<Post> posts = new ArrayList<>();

        for (Object[] result : results) {
            posts.add(mapToPost(result));
        }

        return posts;
    }

    public List<Comment> searchComments(String terms) {
        String sql = """
                    SELECT * FROM comments WHERE FREETEXT (content, :terms) ORDER BY created_at
                """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("terms", terms);
        List<Object[]> results = (List<Object[]>) query.getResultList();
        List<Comment> comments = new ArrayList<>();

        for (Object[] result : results) {
            comments.add(mapToComment(result));
        }

        return comments;
    }

    public List<Profile> searchProfiles(String terms) {
        String sql = """
                    SELECT * FROM profiles WHERE FREETEXT (bio, :terms)
                """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("terms", terms);
        List<Object[]> results = (List<Object[]>) query.getResultList();
        List<Profile> profiles = new ArrayList<>();

        for (Object[] result : results) {
            profiles.add(mapToProfile(result));
        }

        return profiles;
    }

    public List<Searchable> search(String terms) {
        List<Searchable> results = new ArrayList<>();
        results.addAll(this.searchProfiles(terms));
        results.addAll(this.searchPosts(terms));
        results.addAll(this.searchComments(terms));
        return results;
    }
}
