package com.github.qingtian1927.w.controller;

import com.github.qingtian1927.w.model.CustomUserDetails;
import com.github.qingtian1927.w.model.Notification;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.service.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping(value = {"/notification/read/{id}"})
    public String markAsRead(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/login";
        }

        Optional<Notification> notification = notificationService.findById(id);
        User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

        if (notification.isEmpty()) {
            model.addAttribute("error", "Notification not found");
            return "redirect:/error";
        }

        if (!notification.get().getToUser().getId().equals(user.getId())) {
            model.addAttribute("error", "Mismatching user. Request denied");
            return "redirect:/error";
        }

        notification.get().setIsRead(Boolean.TRUE);
        notificationService.save(notification.get());

        return "redirect:/notifications";
    }
}
