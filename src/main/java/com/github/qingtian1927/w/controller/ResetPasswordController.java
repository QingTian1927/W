package com.github.qingtian1927.w.controller;

import com.github.qingtian1927.w.model.ResetPasswordRequest;
import com.github.qingtian1927.w.model.dto.ResetPasswordForm;
import com.github.qingtian1927.w.service.interfaces.ResetPasswordRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
public class ResetPasswordController {

    private final ResetPasswordRequestService resetPasswordRequestService;

    @Autowired
    public ResetPasswordController(ResetPasswordRequestService resetPasswordRequestService) {
        this.resetPasswordRequestService = resetPasswordRequestService;
    }

    @GetMapping(value = {"/reset-password/email"})
    public String email() {
        return "/email/reset_password_email";
    }

    @GetMapping(value = {"/reset-password/email/{id}"})
    public String email(@PathVariable("id") String id, Model model) {
        Optional<ResetPasswordRequest> request = resetPasswordRequestService.findById(UUID.fromString(id));
        if (request.isEmpty()) {
            model.addAttribute("error", "No email found");
            return "redirect:/error";
        }

        model.addAttribute("request", request.get());
        return "/email/reset_password_email";
    }

    @GetMapping(value = {"/reset-password/request"})
    public String request() {
        return "reset_password_request";
    }

    @GetMapping(value = {"/reset-password/request/{id}"})
    public String request(@PathVariable("id") String id, Model model) {
        Optional<ResetPasswordRequest> request = resetPasswordRequestService.findById(UUID.fromString(id));
        if (request.isEmpty()) {
            return "redirect:/reset-password/request";
        }
        model.addAttribute("request", request.get());
        return "reset_password_request";
    }

    @PostMapping(value = {"/reset-password/request/create"})
    public String createRequest(@ModelAttribute ResetPasswordForm params) {
        Optional<ResetPasswordRequest> request = resetPasswordRequestService.save(params.getEmail());
        if (request.isPresent()) {
            return "redirect:/reset-password/email/" + request.get().getId();
        }
        return "redirect:/reset-password/email";
    }

    @PostMapping(value = {"/reset-password/request/{id}/process"})
    public String process(@PathVariable("id") String id, @RequestParam("password") String password, Model model) {
        if (password == null || password.isEmpty()) {
            model.addAttribute("error", "Password not found");
            return "redirect:/reset-password/" + id;
        }

        Optional<ResetPasswordRequest> request = resetPasswordRequestService.findById(UUID.fromString(id));
        if (request.isEmpty()) {
            model.addAttribute("error", "Invalid reset password request");
            return "redirect:/error";
        }

        if (resetPasswordRequestService.resetPassword(request.get(), password)) {
            return "redirect:/login?reset=true";
        }

        model.addAttribute("error", "Failed to reset password");
        return "redirect:/error";
    }
}
