package com.banksphere.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
    @Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    return request.getServletPath().startsWith("/h2-console");
}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (isSecuredApiRequest(request)
                && (authHeader == null || !authHeader.regionMatches(true, 0, "Bearer ", 0, 7))) {
            log.warn("No Bearer Authorization header for secured request [{}]", request.getRequestURI());
        } else if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String token = authHeader.substring(7);
            if (token.isBlank()) {
                log.warn("Empty Bearer token for request [{}]", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
            try {
                String username = jwtUtil.extractUsername(token);
                if (username == null || username.isBlank()) {
                    log.warn("JWT has no subject for request [{}]", request.getRequestURI());
                } else if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtUtil.isTokenValid(token, userDetails.getUsername())) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("JWT authentication succeeded for request [{}]", request.getRequestURI());
                    } else {
                        log.warn("JWT subject validation failed for request [{}]", request.getRequestURI());
                    }
                }
            } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
                log.warn("JWT authentication failed for request [{}]: {}",
                        request.getRequestURI(), e.getClass().getSimpleName());
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isSecuredApiRequest(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/") && !path.startsWith("/api/auth/");
    }
}