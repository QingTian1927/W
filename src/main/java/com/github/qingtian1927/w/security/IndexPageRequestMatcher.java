package com.github.qingtian1927.w.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.regex.Pattern;

public class IndexPageRequestMatcher implements RequestMatcher {
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^/[0-9]+$");

    @Override
    public boolean matches(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "/".equals(path) || NUMERIC_PATTERN.matcher(path).matches();
    }
}
