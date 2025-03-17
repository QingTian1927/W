package com.github.qingtian1927.w.controller;

import com.github.qingtian1927.w.model.Captcha;
import com.github.qingtian1927.w.model.CustomUserDetails;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.service.interfaces.NotificationService;
import com.github.qingtian1927.w.service.interfaces.PostService;
import com.github.qingtian1927.w.service.interfaces.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MainController {
    public static final int PAGE_SIZE = 30;

    private final PostService postService;
    private final NotificationService notificationService;
    private final StatisticsService statisticsService;

    @Autowired
    public MainController(PostService postService, NotificationService notificationService, StatisticsService statisticsService) {
        this.postService = postService;
        this.notificationService = notificationService;
        this.statisticsService = statisticsService;
    }

    @GetMapping(value = {"/", "/index"})
    public String indexRedirect() {
        return "redirect:/index/1";
    }

    @GetMapping(value = {"/index/{page}"})
    public String index(Model model, @PathVariable("page") int page) {
        if (page - 1 < 0) {
            model.addAttribute("error", "Invalid page number");
            return "redirect:/error";
        }

        model.addAttribute("pageNumber", page);
        model.addAttribute("trendingPosts", statisticsService.getWeeklyTrendingPosts(5));
        model.addAttribute("trendingUsers", statisticsService.getWeeklyTrendingUsers(5));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
            model.addAttribute("page", postService.findAllFromFollowed(PageRequest.of(page - 1, PAGE_SIZE), user));
        } else {
            model.addAttribute("page", postService.findAllByOrderByCreatedDateDesc(PageRequest.of(page - 1, PAGE_SIZE)));
        }

        return "index";
    }

    @GetMapping(value = {"/explore"})
    public String exploreRedirect() {
        return "redirect:/explore/1";
    }

    @GetMapping(value = {"/explore/{page}"})
    public String explore(Model model, @PathVariable("page") int page) {
        if (page - 1 < 0) {
            model.addAttribute("error", "Invalid page number");
            return "redirect:/error";
        }

        model.addAttribute("pageNumber", page);
        model.addAttribute("trendingPosts", statisticsService.getWeeklyTrendingPosts(5));
        model.addAttribute("trendingUsers", statisticsService.getWeeklyTrendingUsers(5));
        model.addAttribute("page", postService.findAllByOrderByCreatedDateDesc(PageRequest.of(page - 1, PAGE_SIZE)));

        return "explore";
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

    @GetMapping(value = {"/forgot-password"})
    public String forgotPassword(Model model) {
        model.addAttribute("captcha", new Captcha());
        return "forgot_password";
    }
}
