package com.github.qingtian1927.w.model.dto;

import com.github.qingtian1927.w.model.User;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SignUpForm {
    private String username;
    private String email;
    private String password;
    @Min(13)
    private Integer age;

    public User toUser() {
        return new User(this.email, this.password, this.username, this.username, this.age);
    }
}
