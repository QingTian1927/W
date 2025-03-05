package com.github.qingtian1927.w.controller;

import com.github.qingtian1927.w.model.CustomUserDetails;
import com.github.qingtian1927.w.model.Follow;
import com.github.qingtian1927.w.model.Profile;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.model.dto.SignUpForm;
import com.github.qingtian1927.w.service.interfaces.FollowService;
import com.github.qingtian1927.w.service.interfaces.ProfileService;
import com.github.qingtian1927.w.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Controller
public class UserController {
    private final UserService userService;
    private final ProfileService profileService;
    private final FollowService followService;

    @Autowired
    public UserController(UserService userService, ProfileService profileService, FollowService followService) {
        this.userService = userService;
        this.profileService = profileService;
        this.followService = followService;
    }

    @GetMapping("/users/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        Optional<User> user = userService.findById(id);
        Optional<Profile> profile = profileService.findById(id);

        if (user.isPresent() && profile.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("profile", profile.get());
        }
        return "profile";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute SignUpForm params, Model model) {
        if (userService.exists(params.getEmail(), params.getUsername())) {
            model.addAttribute("error", "User already exists");
            return "signup";
        }

        try {
            User user = params.toUser();
            userService.save(user);
            Profile profile = new Profile(user);
            profileService.save(profile);
            return "redirect:/index";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    @PostMapping("/users/{id}/banner/upload")
    public String uploadBanner(@PathVariable Long id, @RequestParam("image") MultipartFile image, Model model, Authentication auth) {
        Optional<User> user = userService.findById(id);
        if(user.isEmpty() || !user.get().getEmail().equals(auth.getName())) {
            model.addAttribute("error", "Forbidden request: mismatching user credentials");
            return "redirect:/error";
        }

        try {
            Optional<Profile> profile = profileService.findById(id);
            if (profile.isEmpty()) {
                model.addAttribute("error", "Profile not found");
                return "/users/" + id;
            }

            if (image == null || image.isEmpty()) {
                model.addAttribute("error", "No image was uploaded");
                return "/users/" + id;
            }

            profile.get().setBanner(image);
            profileService.save(profile.get());
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to set profile banner");
            e.printStackTrace();
            return "redirect:/error";
        }
        return "redirect:/users/" + id;
    }

    @GetMapping("/users/{id}/banner")
    public ResponseEntity<byte[]> getBanner(@PathVariable Long id) {
        Optional<Profile> profile = profileService.findById(id);
        if (profile.isEmpty() || profile.get().getBanner() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "image/jpeg");
        return new ResponseEntity<>(profile.get().getBanner(), headers, HttpStatus.OK);
    }

    @PostMapping("/users/{id}/avatar/upload")
    public String uploadAvatar(@PathVariable Long id, @RequestParam("image") MultipartFile image, Model model) {
        try {
            Optional<Profile> profile = profileService.findById(id);
            if (profile.isEmpty()) {
                model.addAttribute("error", "Profile not found");
                return "/users/" + id;
            }

            if (image == null || image.isEmpty()) {
                model.addAttribute("error", "No image was uploaded");
                return "/users/" + id;
            }

            profile.get().setAvatar(image);
            profileService.save(profile.get());
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to set profile avatar");
            e.printStackTrace();
            return "redirect:/error";
        }
        return "redirect:/users/" + id;
    }

    @GetMapping("/users/{id}/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long id) {
        Optional<Profile> profile = profileService.findById(id);
        if (profile.isEmpty() || profile.get().getAvatar() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "image/jpeg");
        return new ResponseEntity<>(profile.get().getAvatar(), headers, HttpStatus.OK);
    }

    @PostMapping("/users/{id}/follow")
    public String followUser(@PathVariable Long id, Model model) {
        Optional<User> followedUser = userService.findById(id);

        if (followedUser.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        User followerUser = ((CustomUserDetails) auth.getPrincipal()).getUser();
        if (Objects.equals(followerUser.getId(), followedUser.get().getId())) {
            model.addAttribute("error", "Users cannot follow themselves");
            return "redirect:/error";
        }

        followService.save(followerUser, followedUser.get());
        return "redirect:/users/" + id;
    }

    @PostMapping("/users/{id}/unfollow")
    public String unfollowUser(@PathVariable Long id, Model model) {
        Optional<User> followedUser = userService.findById(id);

        if (followedUser.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        User followerUser = ((CustomUserDetails) auth.getPrincipal()).getUser();
        if (Objects.equals(followerUser.getId(), followedUser.get().getId())) {
            model.addAttribute("error", "Users cannot unfollow themselves");
            return "redirect:/error";
        }

        followService.deleteById(followerUser, followedUser.get());
        return "redirect:/users/" + id;
    }
}
