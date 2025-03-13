package com.github.qingtian1927.w.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "reset_password_requests")
@Data
@NoArgsConstructor
public class ResetPasswordRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    @Column(name = "expire_at", nullable = false)
    private Date expireDate = Date.from(createdDate.toInstant().plus(30, ChronoUnit.MINUTES));

    public ResetPasswordRequest(User user) {
        this.user = user;
    }
}
