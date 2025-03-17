package com.github.qingtian1927.w.config;

import com.github.qingtian1927.w.security.CaptchaAuthenticationFilter;
import com.github.qingtian1927.w.security.IndexPageRequestMatcher;
import com.github.qingtian1927.w.service.implementations.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String REMEMBER_ME_KEY = "my-unsecure-remember-me-key";

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices() {
        PersistentTokenBasedRememberMeServices service = new PersistentTokenBasedRememberMeServices(REMEMBER_ME_KEY, userDetailsService(), new InMemoryTokenRepositoryImpl());
        service.setCookieName("remember-me");
        service.setTokenValiditySeconds(86400);
        return service;
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authProvider);
    }

    @Bean
    public CaptchaAuthenticationFilter captchaAuthenticationFilter(AuthenticationManager authenticationManager) {
        CaptchaAuthenticationFilter filter = new CaptchaAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setFilterProcessesUrl("/users/auth");
        filter.setRememberMeServices(persistentTokenBasedRememberMeServices());
        return filter;
    }

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(new IndexPageRequestMatcher()).permitAll()
                .requestMatchers(
                        "/", "/actuator/**", "/signup", "/login", "/css/**", "/users/**", "/post/**",
                        "/comment/**", "/search/**", "/forgot-password", "/reset-password/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
        ).formLogin(login -> login
                .usernameParameter("email")
                .loginPage("/login")
                .loginProcessingUrl("/users/auth")
                .failureUrl("/login?error=true")
                .defaultSuccessUrl("/")
                .permitAll()

        ).rememberMe(remember -> remember
                .rememberMeServices(persistentTokenBasedRememberMeServices())
        ).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
        ).sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
        ).securityContext(context -> context
                .requireExplicitSave(false)
        );

        http.addFilterBefore(
                captchaAuthenticationFilter(authenticationManager(userDetailsService(), passwordEncoder())),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
