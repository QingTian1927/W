package com.github.qingtian1927.w.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditUserForm {
    @Size(min = 3, max = 32)
    private String username;
    @Size(min = 3, max = 50)
    private String displayName;
    @Max(value = 160)
    private String bio;

    public boolean isContentNull() {
        return username == null && displayName == null && bio == null;
    }
}
