package com.github.qingtian1927.w.controller;

import com.github.qingtian1927.w.model.*;
import com.github.qingtian1927.w.model.dto.CommentForm;
import com.github.qingtian1927.w.model.dto.NotificationForm;
import com.github.qingtian1927.w.model.dto.PostForm;
import com.github.qingtian1927.w.model.dto.SimilarPost;
import com.github.qingtian1927.w.service.interfaces.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class PostController {
    private final UserService userService;
    private final PostService postService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final NotificationService notificationService;
    private final KeywordService keywordService;

    @Autowired
    public PostController(UserService userService, PostService postService, LikeService likeService, CommentService commentService, NotificationService notificationService, KeywordService keywordService) {
        this.userService = userService;
        this.postService = postService;
        this.likeService = likeService;
        this.commentService = commentService;
        this.notificationService = notificationService;
        this.keywordService = keywordService;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        return userService.findById(user.getId()).orElseThrow(null);
    }

    @Transactional
    @PostMapping("/post/create")
    public String createPost(@ModelAttribute PostForm params, @RequestParam("redirect") String redirectPath, Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            model.addAttribute("error", "You must login to create posts");
            return "redirect:/login";
        }

        Post post = params.toPost(user);
        post.setTopics(keywordService.findMainTopics(post.getContent()));
        postService.save(post);

        if (redirectPath == null || redirectPath.isEmpty()) {
            return "redirect:/users/" + user.getId();
        }
        return "redirect:" + redirectPath;
    }

    @GetMapping("/post/{id}")
    public String showPost(@PathVariable Long id, Model model) {
        Optional<Post> post = postService.findById(id);

        if (post.isPresent()) {
            model.addAttribute("post", post.get());
            List<Comment> comments = commentService.listPostCommentDateDesc(post.get());
            if (!comments.isEmpty()) {
                model.addAttribute("comments", comments);
            }

            List<SimilarPost> similarPosts = keywordService.findSimilarPosts(post.get());
            System.out.println(similarPosts);
            if (!similarPosts.isEmpty()) {
                model.addAttribute("similarPosts", similarPosts);
            }
        }
        return "post";
    }

    @Transactional
    @PostMapping("post/{id}/delete")
    public String deletePost(@PathVariable Long id, @RequestParam("redirect") String redirectPath, Model model) {
        Optional<Post> post = postService.findById(id);

        if (post.isEmpty()) {
            model.addAttribute("error", "Post does not exist");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        postService.deleteById(post.get().getId());

        if (redirectPath == null || redirectPath.isEmpty()) {
            return "redirect:/";
        }
        return "redirect:" + redirectPath;
    }

    @Transactional
    @PostMapping("/post/{id}/like")
    public String likePost(@PathVariable Long id, @RequestParam("redirect") String redirectPath, Model model) {
        Optional<Post> post = postService.findById(id);

        if (post.isEmpty()) {
            model.addAttribute("error", "Post does not exist");
            return "redirect:/error";
        }

        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        Like like = new Like(user, post.get());
        likeService.save(like);

        Optional<Notification> notification = NotificationService.buildNotification(
                NotificationForm.builder().type(Notification.LIKE).build(),
                post.get().getUser(),
                Optional.of(user),
                post
        );
        notification.ifPresent(notificationService::save);

        if (redirectPath == null || redirectPath.isEmpty()) {
            return "redirect:/";
        }
        return "redirect:" + redirectPath;
    }

    @Transactional
    @PostMapping("/post/{id}/unlike")
    public String unlikePost(@PathVariable Long id, @RequestParam("redirect") String redirectPath, Model model) {
        Optional<Post> post = postService.findById(id);

        if (post.isEmpty()) {
            model.addAttribute("error", "Post does not exist");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        User user = getAuthenticatedUser();
        likeService.deleteById(user, post.get());

        if (redirectPath == null || redirectPath.isEmpty()) {
            return "redirect:/";
        }
        return "redirect:" + redirectPath;
    }

    @Transactional
    @PostMapping("/post/{id}/repost")
    public String repost(@PathVariable Long id, @RequestParam("redirect") String redirectPath, Model model) {
        Optional<Post> post = postService.findById(id);

        if (post.isEmpty()) {
            model.addAttribute("error", "Post does not exist");
            return "redirect:/error";
        }

        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        Post repost = new Post(post.get(), user);
        postService.save(repost);

        Optional<Notification> notification = NotificationService.buildNotification(
                NotificationForm.builder().type(Notification.REPOST).build(),
                post.get().getUser(),
                Optional.of(user),
                post
        );
        notification.ifPresent(notificationService::save);

        if (redirectPath == null || redirectPath.isEmpty()) {
            return "redirect:/users/" + user.getId();
        }
        return "redirect:" + redirectPath;
    }

    @Transactional
    @PostMapping("/post/{id}/comment")
    public String comment(@PathVariable Long id, @ModelAttribute CommentForm params, Model model) {
        Optional<Post> post = postService.findById(id);

        if (post.isEmpty()) {
            model.addAttribute("error", "Post does not exist");
            return "redirect:/error";
        }

        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        Comment comment = new Comment(user, post.get(), params.getContent());

        Optional<Notification> notification = NotificationService.buildNotification(
                NotificationForm.builder().type(Notification.COMMENT).build(),
                post.get().getUser(),
                Optional.of(user),
                post
        );
        notification.ifPresent(notificationService::save);

        commentService.save(comment);
        return "redirect:/post/" + id;
    }

    @Transactional
    @PostMapping("/comment/{id}/reply")
    public String reply(@PathVariable Long id, @RequestParam("reply-content") @Size(min = 1, max = 1024) String replyContent, Model model) {
        Optional<Comment> comment = commentService.findById(id);

        if (comment.isEmpty()) {
            model.addAttribute("error", "Comment does not exist");
            return "redirect:/error";
        }

        Post post = comment.get().getPost();
        if (post == null) {
            model.addAttribute("error", "Post does not exist");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        User user = getAuthenticatedUser();
        Comment reply = new Comment(user, post, replyContent, comment.get());
        commentService.save(reply);

        return "redirect:/post/" + post.getId();
    }

    @Transactional
    @PostMapping("/comment/{id}/like")
    public String likeComment(@PathVariable Long id, Model model) {
        Optional<Comment> comment = commentService.findById(id);

        if (comment.isEmpty()) {
            model.addAttribute("error", "Comment does not exist");
            return "redirect:/error";
        }

        Post post = comment.get().getPost();
        if (post == null) {
            model.addAttribute("error", "Post does not exist");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        User user = getAuthenticatedUser();
        this.commentService.like(user, comment.get());

        return "redirect:/post/" + post.getId();
    }

    @Transactional
    @PostMapping("/comment/{id}/unlike")
    public String unlikeComment(@PathVariable Long id, Model model) {
        Optional<Comment> comment = commentService.findById(id);

        if (comment.isEmpty()) {
            model.addAttribute("error", "Comment does not exist");
            return "redirect:/error";
        }

        Post post = comment.get().getPost();
        if (post == null) {
            model.addAttribute("error", "Post does not exist");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        User user = getAuthenticatedUser();
        this.commentService.unlike(user, comment.get());

        return "redirect:/post/" + post.getId();
    }

    @Transactional
    @PostMapping("/comment/{id}/delete")
    public String deleteComment(@PathVariable Long id, @RequestParam("redirect") String redirectPath, Model model) {
        Optional<Comment> comment = commentService.findById(id);

        if (comment.isEmpty()) {
            model.addAttribute("error", "Comment does not exist");
            return "redirect:/error";
        }

        Post post = comment.get().getPost();
        if (post == null) {
            model.addAttribute("error", "Post does not exist");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        this.commentService.deleteById(comment.get().getId());

        if (redirectPath == null || redirectPath.isEmpty()) {
            return "redirect:/";
        }
        return "redirect:" + redirectPath;
    }
}