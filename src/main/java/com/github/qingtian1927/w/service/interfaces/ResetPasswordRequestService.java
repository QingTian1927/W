package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.ResetPasswordRequest;
import com.github.qingtian1927.w.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResetPasswordRequestService {
    int REQUEST_LIMIT = 5;

    Optional<ResetPasswordRequest> save(String email);
    Optional<ResetPasswordRequest> findById(UUID id);
    List<ResetPasswordRequest> findByUser(User user);
    boolean resetPassword(ResetPasswordRequest request, String password);
}
