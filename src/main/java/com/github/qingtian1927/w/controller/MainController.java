package com.github.qingtian1927.w.controller;

import com.github.qingtian1927.w.model.Captcha;
import com.github.qingtian1927.w.model.CustomUserDetails;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.service.interfaces.NotificationService;
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
    private final NotificationService notificationService;

    @Autowired
    public MainController(PostService postService, UserService userService, NotificationService notificationService) {
        this.postService = postService;
        this.userService = userService;
        this.notificationService = notificationService;
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
            return "redirect:/";
        }

        model.addAttribute("captcha", new Captcha());
        return "login";
    }

    @GetMapping(value = {"/signup"})
    public String signup(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "signup";
    }

    @GetMapping(value = {"/notifications"})
    public String notifications(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        model.addAttribute("notifications", notificationService.findByUser(user));

        return "notification";
    }
}
