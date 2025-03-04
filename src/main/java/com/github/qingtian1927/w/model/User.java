package com.github.qingtian1927.w.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    @Email
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "display_name", nullable = false)
    @Size(min = 3, max = 50)
    private String displayName;

    @Column(name = "username", unique = true, nullable = false)
    @Size(min = 3, max = 32)
    private String username;

    @Column(name = "age", nullable = false)
    @Min(value = 13)
    private Integer age;

    @Column(name = "is_not_banned")
    private Boolean isNotBanned = Boolean.TRUE;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    @Column(name = "role")
    @Size(max = 20)
    private String role = "USER";

    public User(String email, String password, String displayName, String username, Integer age) {
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.username = username;
        this.age = age;
    }
}
