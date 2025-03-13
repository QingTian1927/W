package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.ResetPasswordRequest;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.repository.ResetPasswordRequestRepository;
import com.github.qingtian1927.w.service.interfaces.ResetPasswordRequestService;
import com.github.qingtian1927.w.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResetPasswordRequestServiceImpl implements ResetPasswordRequestService {

    private final ResetPasswordRequestRepository resetPasswordRequestRepository;
    private final UserService userService;

    @Autowired
    public ResetPasswordRequestServiceImpl(ResetPasswordRequestRepository resetPasswordRequestRepository, UserService userService) {
        this.resetPasswordRequestRepository = resetPasswordRequestRepository;
        this.userService = userService;
    }

    @Transactional
    @Override
    public Optional<ResetPasswordRequest> save(String email) {
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            return Optional.empty();
        }

        if (resetPasswordRequestRepository.countByUser(user.get()) > REQUEST_LIMIT) {
            resetPasswordRequestRepository.deleteByUser(user.get());
        }
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(user.get());
        return Optional.of(resetPasswordRequestRepository.save(resetPasswordRequest));
    }

    @Override
    public Optional<ResetPasswordRequest> findById(UUID id) {
        return resetPasswordRequestRepository.findById(id);
    }

    @Transactional
    @Override
    public List<ResetPasswordRequest> findByUser(User user) {
        return resetPasswordRequestRepository.findByUser(user);
    }

    @Override
    public boolean resetPassword(ResetPasswordRequest request, String password) {
        User user = request.getUser();
        user.setPassword(password);
        User resetPasswordUser = userService.save(user);

        if (resetPasswordUser != null) {
            resetPasswordRequestRepository.deleteById(request.getId());
            return true;
        }
        return false;
    }
}
