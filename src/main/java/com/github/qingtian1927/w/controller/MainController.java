package com.github.qingtian1927.w.controller;

import com.github.qingtian1927.w.service.interfaces.PostService;
import com.github.qingtian1927.w.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public MainController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping(value = {"/", "/index"})
    public String index(Model model) {
        model.addAttribute("posts", postService.findAllByOrderByCreatedDateDesc());
        return "index";
    }

    @GetMapping(value = {"/login"})
    public String login(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/login";
        }
        return "login";
    }

    @GetMapping(value = {"/signup"})
    public String signup(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/login";
        }
        return "signup";
    }

    @GetMapping(value = {"/admin/dashboard"})
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (
                authentication instanceof AnonymousAuthenticationToken ||
                authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("USER"))
        ) {
            return "redirect:/login";
        }

        model.addAttribute("posts", postService.listRecentPosts());
        model.addAttribute("users", userService.listRecentUsers());
        return "/admin/dashboard";
    }

    @GetMapping(value = {"/admin/users"})
    public String users(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (
                authentication instanceof AnonymousAuthenticationToken ||
                        authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("USER"))
        ) {
            return "redirect:/login";
        }

        model.addAttribute("users", userService.findAll());
        return "/admin/users";
    }

}
