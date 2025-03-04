package com.github.qingtian1927.w.service.implementations;

import com.github.qingtian1927.w.model.CustomUserDetails;
import com.github.qingtian1927.w.model.Profile;
import com.github.qingtian1927.w.model.User;
import com.github.qingtian1927.w.service.interfaces.ProfileService;
import com.github.qingtian1927.w.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomUserDetailsService implements UserDetailsService {
    private UserService userService;
    private ProfileService profileService;

    @Autowired
    public void setUserRepository(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userService.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("[AUTH] Could not find user: " + username);
        }

        Optional<Profile> profile = profileService.findById(user.get().getId());
        if (profile.isEmpty()) {
            profile = Optional.of(profileService.save(new Profile(user.get())));
        }

        return new CustomUserDetails(user.get(), profile.get());
    }
}
