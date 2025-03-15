package com.github.qingtian1927.w.repository;

import com.github.qingtian1927.w.model.ResetPasswordRequest;
import com.github.qingtian1927.w.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResetPasswordRequestRepository extends JpaRepository<ResetPasswordRequest, UUID> {

    List<ResetPasswordRequest> findByUser(User user);
    void deleteByUser(User user);
    int countByUser(User user);

    @Query(value = "DELETE FROM reset_password_requests WHERE expire_at <= CURRENT_TIMESTAMP", nativeQuery = true)
    void deleteExpiredRequestsByUser(User user);
}
