package com.devhoard.security;

import com.devhoard.entities.User;
import com.devhoard.repository.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import java.util.ArrayList; // For the empty authorities


import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepo userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtUtils.getUsernameFromToken(token);

            // 👮‍♂️ Check if the Bulletin Board is empty for this request
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 🔎 Find the user in the database
                User user = userRepo.findByUsername(username).orElse(null);

                if (user != null) {
                    // 🎫 THE IDENTITY CERTIFICATE:
                    // We create a "Security Token" that contains the user details and their permissions.
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, null, new ArrayList<>() // Empty list of roles for now
                    );

                    // 📌 THE BULLETIN BOARD:
                    // We post the certificate so the controllers know who is signed in!
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // 🚀 Keep the line moving!
        filterChain.doFilter(request, response);
    }


}
