package com.github.qingtian1927.w.model.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class SettingsEditForm {
    @Email
    private String email;
    private String oldPassword;
    private String newPassword;
    private String repeatPassword;

    public boolean containsNullData() {
        return email == null || oldPassword == null || newPassword == null || repeatPassword == null;
    }
}
