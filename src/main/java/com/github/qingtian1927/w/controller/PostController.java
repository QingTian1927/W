package com.github.qingtian1927.w.controller;

import com.github.qingtian1927.w.model.*;
import com.github.qingtian1927.w.model.dto.CommentForm;
import com.github.qingtian1927.w.model.dto.PostForm;
import com.github.qingtian1927.w.service.interfaces.CommentService;
import com.github.qingtian1927.w.service.interfaces.LikeService;
import com.github.qingtian1927.w.service.interfaces.PostService;
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
    private final PostService postService;
    private final LikeService likeService;
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, LikeService likeService, CommentService commentService) {
        this.postService = postService;
        this.likeService = likeService;
        this.commentService = commentService;
    }

    @PostMapping("/post/create")
    public String createPost(@ModelAttribute PostForm params, @RequestParam("redirect") String redirectPath, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            model.addAttribute("error", "You must login to create posts");
            return "redirect:/login";
        }

        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        Post post = params.toPost(user);
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
        }
        return "post";
    }

    @PostMapping("/post/{id}/like")
    public String likePost(@PathVariable Long id, @RequestParam("redirect") String redirectPath, Model model) {
        Optional<Post> post = postService.findById(id);

        if (post.isEmpty()) {
            model.addAttribute("error", "Post does not exist");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        Like like = new Like(user, post.get());
        likeService.save(like);

        if (redirectPath == null || redirectPath.isEmpty()) {
            return "redirect:/";
        }
        return "redirect:" + redirectPath;
    }

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

        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        likeService.deleteById(user, post.get());

        if (redirectPath == null || redirectPath.isEmpty()) {
            return "redirect:/";
        }
        return "redirect:" + redirectPath;
    }

    @PostMapping("/post/{id}/repost")
    public String repost(@PathVariable Long id, @RequestParam("redirect") String redirectPath, Model model) {
        Optional<Post> post = postService.findById(id);

        if (post.isEmpty()) {
            model.addAttribute("error", "Post does not exist");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        Post repost = new Post(post.get(), user);
        postService.save(repost);

        if (redirectPath == null || redirectPath.isEmpty()) {
            return "redirect:/users/" + user.getId();
        }
        return "redirect:" + redirectPath;
    }

    @PostMapping("/post/{id}/comment")
    public String comment(@PathVariable Long id, @ModelAttribute CommentForm params, Model model) {
        Optional<Post> post = postService.findById(id);

        if (post.isEmpty()) {
            model.addAttribute("error", "Post does not exist");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        Comment comment = new Comment(user, post.get(), params.getContent());

        commentService.save(comment);
        return "redirect:/post/" + id;
    }

    @PostMapping("/comment/{id}/reply")
    public String reply(@PathVariable Long id, @RequestParam("reply-content") @Size(min = 1, max = 300) String replyContent, Model model) {
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

        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        Comment reply = new Comment(user, post, replyContent, comment.get());
        commentService.save(reply);

        return "redirect:/post/" + post.getId();
    }

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

        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        this.commentService.like(user, comment.get());

        return "redirect:/post/" + post.getId();
    }

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

        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        this.commentService.unlike(user, comment.get());

        return "redirect:/post/" + post.getId();
    }
}