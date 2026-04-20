package com.devhoard.security; // Security filtering layer

// Internal domain models and data access repositories
import com.devhoard.entities.User;
import com.devhoard.repository.UserRepo;

// Jakarta Servlet primitives for request/response interception
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Spring framework and Security imports for filter lifecycle and context management
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

// Standard Java utilities for collection handling and IO
import java.util.ArrayList;
import java.io.IOException;

/**
 * Filter responsible for JWT interception and authentication context
 * population.
 * Extends OncePerRequestFilter to ensure a single execution per request
 * dispatch.
 */
@Component
// @RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepo userRepo;

    /**
     * Dependency injection via constructor to ensure instance immutability.
     */
    public JwtAuthenticationFilter(JwtUtils jwtUtils, com.devhoard.repository.UserRepo userRepo) {
        this.jwtUtils = jwtUtils;
        this.userRepo = userRepo;
    }

    /**
     * Core filter logic: Extracts the Bearer token, validates identity, and
     * hydrates the SecurityContext.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Logging request metadata for audit and diagnostic purposes
        System.out.println("[FILTER HEARTBEAT] Intercepted: " + request.getMethod() + " " + request.getRequestURI());

        // Extraction of the 'Authorization' header from the incoming request
        String authHeader = request.getHeader("Authorization");

        // Protocol Check: Verify the header follows the 'Bearer [Token]' standard
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Strip the 'Bearer ' prefix to isolate the raw JWT

            try {
                // Resolution: Extract the subject (username) from the cryptographically signed
                // token
                String username = jwtUtils.getUsernameFromToken(token);

                // Authentication state check: Ensure the principal is resolved and the context
                // is not already populated
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Persistence check: Verify the subject exists within the application's user
                    // directory
                    User user = userRepo.findByUsername(username).orElse(null);

                    if (user != null) {
                        /*
                         * Authentication Token Instantiation:
                         * Creating an internal representation of the authenticated principal.
                         * Authorities are currently initialized as an empty list (RBAC pending).
                         */
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                user, null, new ArrayList<>());

                        // Context Hydration: Registering the authenticated identity for sub-thread
                        // access (e.g. Controllers)
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                // Graceful degradation: Signal token expiry to the client via custom response
                // header
                response.setHeader("X-Session-Expiry", "true");
                System.out.println("[Security Service] Identity token expired. Resuming in Guest context.");
            } catch (Exception e) {
                // Generic failure suppression to prevent leaking detail of token validation
                // logic
                System.out.println("[Security Service] Malformed or invalid identity token intercepted.");
            }
        }

        // Filter Chain Progression: Hand over execution to the next filter in the
        // pipeline
        filterChain.doFilter(request, response);
    }
}
