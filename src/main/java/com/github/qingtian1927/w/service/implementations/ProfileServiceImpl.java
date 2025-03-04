package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.Profile;
import com.github.qingtian1927.w.repository.ProfileRepository;
import com.github.qingtian1927.w.service.interfaces.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> findById(Long id) {
        return profileRepository.findById(id);
    }

    @Override
    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }
}
