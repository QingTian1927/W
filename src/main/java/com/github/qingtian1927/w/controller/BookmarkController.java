package com.github.qingtian1927.w.controller;

import com.github.qingtian1927.w.model.Bookmark;
import com.github.qingtian1927.w.model.Bookmarkable;
import com.github.qingtian1927.w.model.CustomUserDetails;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.service.interfaces.BookmarkService;
import com.github.qingtian1927.w.service.interfaces.CommentService;
import com.github.qingtian1927.w.service.interfaces.PostService;
import com.github.qingtian1927.w.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;
import java.util.UUID;

@Controller
public class BookmarkController {
    private final BookmarkService bookmarkService;
    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService, PostService postService, CommentService commentService, UserService userService) {
        this.bookmarkService = bookmarkService;
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        return userService.findById(user.getId()).orElseThrow(null);
    }

    @GetMapping("/bookmarks")
    public String bookmarks(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            model.addAttribute("error", "No authorized user found");
            return "redirect:/error";
        }

        model.addAttribute("bookmarks", bookmarkService.findByUser(user));
        return "bookmarks";
    }

    @PostMapping("/bookmark/{entity}/{id}")
    public String addBookmark(@PathVariable String entity, @PathVariable Long id, Model model) {
        if (Bookmark.isInvalidType(entity)) {
            model.addAttribute("error", "Invalid Bookmark type");
            return "redirect:/error";
        }

        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Bookmarkable> referencedObject = Optional.empty();
        switch (entity) {
            case Bookmark.TYPE_COMMENT -> referencedObject = Optional.of(commentService.findById(id).orElseThrow());
            case Bookmark.TYPE_POST -> referencedObject = Optional.of(postService.findById(id).orElseThrow());
        }

        if (referencedObject.isEmpty()) {
            model.addAttribute("error", "Invalid Bookmark reference");
            return "redirect:/error";
        }

        Bookmark bookmark = new Bookmark(user, entity, id);
        bookmarkService.save(bookmark);

        return "redirect:/bookmarks";
    }

    @PostMapping("/bookmark/delete/{id}")
    public String removeBookmark(@PathVariable UUID id, Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Bookmark> bookmark = bookmarkService.findById(id);

        if (bookmark.isEmpty()) {
            model.addAttribute("error", "Invalid bookmark");
            return "redirect:/error";
        }

        bookmarkService.deleteById(bookmark.get().getId());
        return "redirect:/bookmarks";
    }
}
