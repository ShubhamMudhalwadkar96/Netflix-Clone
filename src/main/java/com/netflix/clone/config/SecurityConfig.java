package com.netflix.clone.config;

import com.netflix.clone.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * List of API endpoints that can be accessed without authentication.
     * These endpoints are typically used for user registration, login,
     * email verification, and password recovery operations.
     */
    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/login",
            "/api/auth/signup",
            "/api/auth/validate-email",
            "/api/auth/verify-email",
            "/api/auth/resend-verification",
            "/api/auth/forgot-password",
            "/api/auth/reset-password"
    };

    /**
     * Creates and configures the password encoder bean.
     *
     * @return password encoder instance used for password operations
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }

    /**
     * Configures the Spring Security filter chain.
     * <p>
     * Configuration includes:
     * <ul>
     *     <li>Disabling CSRF protection</li>
     *     <li>Enabling CORS support</li>
     *     <li>Allowing unauthenticated access to public endpoints</li>
     *     <li>Requiring authentication for all other requests</li>
     *     <li>Using stateless session management</li>
     *     <li>Registering the JWT authentication filter</li>
     * </ul>
     *
     * @param httpSecurity HttpSecurity configuration object
     * @return configured SecurityFilterChain
     * @throws Exception if an error occurs while building the security filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
        httpSecurity.csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth.requestMatchers(PUBLIC_ENDPOINTS).
                        permitAll().anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();

    }
}
