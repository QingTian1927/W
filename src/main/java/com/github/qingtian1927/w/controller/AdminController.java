package com.github.qingtian1927.w.controller;

import com.github.qingtian1927.w.service.interfaces.PostService;
import com.github.qingtian1927.w.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AdminController {
    private static final int USER_PAGE_SIZE = 30;
    private static final int POST_PAGE_SIZE = 15;

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public AdminController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping(value = {"/admin/dashboard"})
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (
                authentication.getAuthorities().stream()
                        .anyMatch(r -> r.getAuthority().equals("ADMIN"))
        ) {
            model.addAttribute("posts", postService.listRecentPosts());
            model.addAttribute("users", userService.listRecentUsers());
            return "/admin/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping(value = {"/admin/users"})
    public String usersRedirect() {
        return "redirect:/admin/users/1";
    }

    @GetMapping(value = {"/admin/users/{page}"})
    public String users(@PathVariable int page, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (
                authentication.getAuthorities().stream()
                        .anyMatch(r -> r.getAuthority().equals("ADMIN"))
        ) {
            if (page - 1 < 0) {
                model.addAttribute("error", "Invalid page number");
                return "redirect:/error";
            }

            model.addAttribute("pageNumber", page);
            model.addAttribute("page", userService.findAll(PageRequest.of(page - 1, USER_PAGE_SIZE)));
            return "/admin/users";
        }
        return "redirect:/login";
    }

    @GetMapping(value = {"/admin/posts"})
    public String postsRedirect() {
        return "redirect:/admin/posts/1";
    }

    @GetMapping(value = {"/admin/posts/{page}"})
    public String posts(@PathVariable int page, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (
                authentication.getAuthorities().stream()
                        .anyMatch(r -> r.getAuthority().equals("ADMIN"))
        ) {
            if (page - 1 < 0) {
                model.addAttribute("error", "Invalid page number");
                return "redirect:/error";
            }

            model.addAttribute("pageNumber", page);
            model.addAttribute("page", postService.findAll(PageRequest.of(page - 1, POST_PAGE_SIZE)));
            return "/admin/posts";
        }
        return "redirect:/login";
    }
}
