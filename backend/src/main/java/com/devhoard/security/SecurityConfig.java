package com.devhoard.security; // Security infrastructure layer

// Standard Spring Security and configuration imports
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Central security configuration for the application.
 * Defines the filter chain, authentication protocols, and cross-origin resource
 * sharing (CORS) rules.
 */
@EnableWebSecurity
@Configuration
// @RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Dependency injection via constructor to ensure instance state immutability.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        System.out.println("[SYSTEM BOOT] SECURITY CONFIGURATION INITIALIZED");
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configures the primary security filter chain.
     * Implements a stateless JWT-based authentication model.
     */
    @Bean
    @Primary
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("[SYSTEM BOOT] SECURITY FILTER CHAIN GENERATION COMMENCED");

        return http
                // CSRF is disabled as we are using stateless JWTs instead of session cookies.
                // This mitigates the risk of Cross-Site Request Forgery without requiring CSRF
                // tokens.
                .csrf(AbstractHttpConfigurer::disable)

                // standard CORS configuration utilizing the source defined below.
                .cors(Customizer.withDefaults())

                // Enforcing a Stateless session policy: Spring Security will not create or use
                // HTTP sessions.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Explicitly permitting pre-flight OPTIONS requests to facilitate CORS
                        // handshakes
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // Permitting all authentication-related requests (Login/Register)
                        .requestMatchers("/api/auth/**").permitAll()

                        /* 
                         * Historical Logic Analysis:
                         * Previously, we attempted to split permissions based on HTTP methods:
                         * // .requestMatchers(HttpMethod.GET, "/api/bookmarks/**").permitAll() 
                         * // .requestMatchers("/api/bookmarks/**").authenticated() 
                         * 
                         * Technical Context:
                         * The above strategy intended to have a 'Public Beach' for viewing and 
                         * authenticated mutation. However, since the system supports both 
                         * registered users and anonymous guests for all CRUD operations, 
                         * authorization was migrated to the service layer (BookmarkService.verifyOwnership).
                         * This move allows for more granular identity-token-based validation.
                         */
                        .requestMatchers("/api/bookmarks/**").permitAll()

                        // All other endpoints require a valid authentication token.
                        .anyRequest().authenticated())

                // Injecting the custom JWT filter before the standard
                // UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * PasswordEncoder bean using BCrypt hashing algorithm.
     * Handles the salting and hashing of user credentials.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines the CORS configuration source.
     * Currently implemented with a 'Total Unlock' strategy to facilitate rapid
     * development by permitting all origins.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        System.out.println("[SECURITY] Applying permissive CORS configuration for development environments");

        CorsConfiguration configuration = new CorsConfiguration();

        // Permitting all origins using a wildcard pattern. This should be restricted to
        // specific domains in production.
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));

        // permitting credentials to allow for header-based auth and cookies if required
        // in the future
        configuration.setAllowCredentials(true);

        // Exposing specific headers to allow the frontend client (React) to parse them
        // from the response metadata
        configuration.setExposedHeaders(List.of("X-Session-Expiry"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Applying the config to all API paths
        return source;
    }
}
