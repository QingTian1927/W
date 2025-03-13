package com.github.qingtian1927.w.model.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ResetPasswordForm {
    @Email
    private String email;
}
