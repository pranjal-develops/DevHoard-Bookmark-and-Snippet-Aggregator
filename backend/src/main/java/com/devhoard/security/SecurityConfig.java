package com.devhoard.security;

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

@EnableWebSecurity
@Configuration
// @RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        System.out.println("[SYSTEM BOOT] SECURITY CONFIG DETECTED!");
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    @Primary
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("あ! [SYSTEM BOOT] SECURITY FILTER CHAIN INITIALIZED!");
        return http
                .csrf(AbstractHttpConfigurer::disable) // 1. Turn off "Cookie Fingerprints"
                .cors(Customizer.withDefaults()) // 2. Allow the React "Boat" to dock
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 3. "No
                                                                                                              // Memory"
                                                                                                              // mode

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll() // Open the "Entry Portal"
                        // .requestMatchers(HttpMethod.GET, "/api/bookmarks/**").permitAll() // Open the
                        // "Public Beach"
                        // .requestMatchers("/api/bookmarks/**").authenticated() // Mutation
                        // (Delete/Patch/Post)
                        .requestMatchers("/api/bookmarks/**").permitAll() // Allow all the methods instead of just GET

                        .anyRequest().authenticated() // LOCK EVERYTHING ELSE!
                )

                // 🛂 "Put my Guard in front of the Standard Spring Guard"
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        System.out.println("あ! [SECURITY] Configuring Total CORS Unlock...");
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow absolutely everything for debugging
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("X-Session-Expiry")); // Tell the browser it is okay for React to read
                                                                      // this specific header
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
