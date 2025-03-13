package com.github.qingtian1927.w.security;

import com.github.qingtian1927.w.model.Captcha;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CaptchaAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public static final String FORM_EMAIL = "email";
    public static final String FORM_PASSWORD = "password";
    public static final String FORM_CAPTCHA_STRING = "captcha-string";
    public static final String FORM_CAPTCHA_ANSWER = "captcha-answer";

    private static final Logger logger = LoggerFactory.getLogger(CaptchaAuthenticationFilter.class);

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email = obtainUsername(request);
        String password = obtainPassword(request);

        logger.info("Attempting to authenticate user");

        if (email == null || password == null) {
            throw new BadCredentialsException("Credentials are missing");
        }
        if (email.isEmpty() || password.isEmpty()) {
            throw new BadCredentialsException("Credentials are empty");
        }

        String captchaDataString = obtainCaptcha(request);
        String captchaAnswerString = obtainCaptchaAnswer(request);

        logger.debug("Received captcha data: {}", captchaDataString);
        logger.debug("Received captcha answer: {}", captchaAnswerString);

        if (captchaDataString == null || captchaAnswerString == null) {
            throw new BadCredentialsException("Captcha is missing");
        }
        if (captchaDataString.isEmpty() || captchaAnswerString.isEmpty()) {
            throw new BadCredentialsException("Captcha is empty");
        }

        Captcha captcha = Captcha.parse(captchaDataString);
        Optional<Integer> expectedAnswer = Captcha.solve(captcha);

        logger.debug("Calculated captcha expected answer: {}", expectedAnswer);

        if (expectedAnswer.isEmpty()) {
            throw new BadCredentialsException("Invalid captcha data");
        }
        if (!expectedAnswer.get().toString().equals(captchaAnswerString)) {
            throw new BadCredentialsException("Invalid captcha answer");
        }

        logger.info("User successfully completed captcha");

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);
        return super.getAuthenticationManager().authenticate(authRequest);
    }

    protected String obtainCaptcha(HttpServletRequest request) {
        return request.getParameter(FORM_CAPTCHA_STRING);
    }

    protected String obtainCaptchaAnswer(HttpServletRequest request) {
        return request.getParameter(FORM_CAPTCHA_ANSWER);
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(FORM_EMAIL);
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(FORM_PASSWORD);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        logger.info("Successfully authenticated user");
        super.successfulAuthentication(request, response, chain, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        logger.warn("Authentication failed: {}", failed.getMessage());
        response.sendRedirect("/login?error=true");
    }
}
