package com.netflix.clone.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT authentication filter that intercepts incoming requests,
 * extracts JWT tokens, validates them, and populates the Spring
 * Security context with authenticated user details.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Processes each incoming HTTP request by extracting the JWT token,
     * validating it, and setting authentication details in the security context.
     *
     * @param request incoming HTTP request
     * @param response outgoing HTTP response
     * @param filterChain filter chain for continuing request processing
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = extractJwtToken(request);
        String username = jwtUtil.getUsernameFromToken(jwt);

        if (shouldProcessAuthentication(username)) {
            processAuthentication(request, jwt, username);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts a JWT token from the incoming request.
     * The token can be provided either through the Authorization header
     * using the Bearer scheme or as a request parameter for file access APIs.
     *
     * @param request incoming HTTP request
     * @return extracted JWT token, or null if no token is found
     */
    private String extractJwtToken(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else if ((requestURI.contains("/api/files/video/") || requestURI.contains("/api/files/image/"))
        && request.getParameter("token")!= null) {
            return request.getParameter("token");
        }
        return null;
    }

    /**
     * Determines whether authentication processing should be performed.
     * Authentication is processed only when a username is present and
     * no authentication already exists in the security context.
     *
     * @param username username extracted from the JWT token
     * @return true if authentication should be processed, otherwise false
     */
    private boolean shouldProcessAuthentication(String username) {
        return username!= null && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    /**
     * Validates the JWT token and, if valid, creates and stores
     * an authenticated user in the Spring Security context.
     *
     * @param request incoming HTTP request
     * @param jwt JWT token
     * @param username username extracted from the token
     */
    private void processAuthentication(HttpServletRequest request, String jwt, String username) {
        if (jwtUtil.validateToken(jwt)) {
            UserDetails userDetails = createUserDetailsFromToken(jwt, username);
            setAuthenticationInContext(request, userDetails);
        }
    }

    /**
     * Creates a Spring Security UserDetails object from the JWT token claims.
     * The user's role is converted into a Spring Security authority.
     *
     * @param jwt JWT token
     * @param username username extracted from the token
     * @return UserDetails representing the authenticated user
     */
    private UserDetails createUserDetailsFromToken(String jwt, String username) {
        String role = jwtUtil.getRoleFromToken(jwt);
        return User.builder()
                .username(username)
                .password("")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)))
                .build();
    }

    /**
     * Stores the authenticated user information in the Spring Security context.
     * This allows subsequent authorization checks to access the authenticated user.
     *
     * @param request incoming HTTP request
     * @param userDetails authenticated user details
     */
    private void setAuthenticationInContext(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
