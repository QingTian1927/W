package com.github.qingtian1927.w.controller;

import com.github.qingtian1927.w.model.*;
import com.github.qingtian1927.w.model.dto.EditUserForm;
import com.github.qingtian1927.w.model.dto.NotificationForm;
import com.github.qingtian1927.w.model.dto.SignUpForm;
import com.github.qingtian1927.w.service.interfaces.*;
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
    private final NotificationService notificationService;
    private final PostService postService;

    @Autowired
    public UserController(UserService userService, ProfileService profileService, FollowService followService, NotificationService notificationService, PostService postService) {
        this.userService = userService;
        this.profileService = profileService;
        this.followService = followService;
        this.notificationService = notificationService;
        this.postService = postService;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        return userService.findById(user.getId()).orElseThrow(null);
    }

    @GetMapping("/users/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        Optional<User> user = userService.findById(id);
        Optional<Profile> profile = profileService.findById(id);

        if (user.isPresent() && profile.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("profile", profile.get());
            model.addAttribute("posts", postService.findByUser(user.get()));
        }
        return "profile";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute SignUpForm params, Model model) {
        if (userService.existsByUsername(params.getEmail(), params.getUsername())) {
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

    @PostMapping("/users/{id}/edit")
    public String editUser(@ModelAttribute EditUserForm params, Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Profile> profile = profileService.findById(user.getId());
        if (profile.isEmpty()) {
            profile = Optional.of(new Profile(user));
        }

        if (params.isContentNull()) {
            model.addAttribute("error", "Missing parameters");
            return "redirect:/users/" + user.getId();
        }

        String username = params.getUsername();
        String displayName = params.getDisplayName();
        String bio = params.getBio();

        System.out.println(user);

        if (!username.isEmpty()) {
            if (userService.existsByUsername(username)) {
                model.addAttribute("error", "User already exists");
                model.addAttribute("user", user);
                model.addAttribute("profile", profile.get());
            }
            user.setUsername(username);
        }
        if (!displayName.isEmpty()) {
            user.setDisplayName(displayName);
        }
        if (!bio.isEmpty()) {
            profile.get().setBio(bio);
        }

        user = userService.edit(user);
        System.out.println(user);

        profileService.save(profile.get());

        return "redirect:/users/" + user.getId();
    }

    @PostMapping("/users/{id}/banner/upload")
    public String uploadBanner(@PathVariable Long id, @RequestParam("image") MultipartFile image, Model model, Authentication auth) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty() || !user.get().getEmail().equals(auth.getName())) {
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
        Optional<Notification> notification = NotificationService.buildNotification(
                NotificationForm.builder().type(Notification.FOLLOW).build(),
                followedUser.get(),
                Optional.of(followerUser),
                Optional.empty()
        );
        notification.ifPresent(notificationService::save);

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

    @PostMapping("/users/{id}/notify")
    public String notifyUser(@PathVariable Long id, @ModelAttribute NotificationForm params, @RequestParam("redirect") String redirectPath, Model model) {
        Optional<User> toUser = userService.findById(id);
        if (toUser.isEmpty()) {
            model.addAttribute("error", "Receiver user not found");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        User fromUser = ((CustomUserDetails) auth.getPrincipal()).getUser();
        Optional<Notification> notification = NotificationService.buildNotification(params, toUser.get(), Optional.of(fromUser), Optional.empty());
        notification.ifPresent(notificationService::save);

        if (redirectPath == null || redirectPath.isEmpty()) {
            return "redirect:/";
        }
        return "redirect:" + redirectPath;
    }

    @PostMapping("/users/{id}/lock")
    public String lockUser(@PathVariable Long id, @RequestParam("redirect") String redirectPath, Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        if (
                auth.getAuthorities().stream()
                        .anyMatch(r -> r.getAuthority().equals("ADMIN"))
        ) {
            user.get().setIsNotBanned(Boolean.FALSE);
            userService.save(user.get());

            if (redirectPath == null || redirectPath.isEmpty()) {
                return "redirect:/";
            }
            return "redirect:" + redirectPath;
        }

        model.addAttribute("error", "Request forbidden");
        return "redirect:/error";
    }

    @PostMapping("/users/{id}/unlock")
    public String unlockUser(@PathVariable Long id, @RequestParam("redirect") String redirectPath, Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "redirect:/error";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        if (
                auth.getAuthorities().stream()
                        .anyMatch(r -> r.getAuthority().equals("ADMIN"))
        ) {
            user.get().setIsNotBanned(Boolean.TRUE);
            userService.save(user.get());

            if (redirectPath == null || redirectPath.isEmpty()) {
                return "redirect:/";
            }
            return "redirect:" + redirectPath;
        }

        model.addAttribute("error", "Request forbidden");
        return "redirect:/error";
    }
}
