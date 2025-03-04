package com.github.qingtian1927.w.service.interfaces;

import com.github.qingtian1927.w.model.Profile;

import java.util.Optional;

public interface ProfileService {
    Optional<Profile> findById(Long id);
    Profile save(Profile profile);
}
