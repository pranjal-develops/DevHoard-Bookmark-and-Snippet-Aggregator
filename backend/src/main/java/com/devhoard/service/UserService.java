package com.devhoard.service; // Service layer for user lifecycle management

// Data Transfer Objects for authentication requests and responses
import com.devhoard.DTO.AuthRequest;
import com.devhoard.DTO.AuthResponse;
import com.devhoard.entities.Bookmark;
import com.devhoard.entities.User;
import com.devhoard.repository.BookmarkRepo;
import com.devhoard.repository.UserRepo;
import com.devhoard.security.JwtUtils;

// Standard imports for boilerplate reduction and security primitives
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserService handles user registration, authentication, and session
 * management.
 * It also facilitates the 'guest-to-user' migration flow to prevent data loss
 * upon account creation.
 */
@Service
// @RequiredArgsConstructor
public class UserService {

    // Dependency injection via final fields ensures thread-safety and immutability
    private final BookmarkRepo bookmarkRepo;
    private final UserRepo userRepo;
    private final JwtUtils jwtUtils;
    // private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    // This is not needed since we are creating passwordEncoder as a bean in
    // SecurityConfig class
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor-based dependency injection is the preferred architectural pattern
     * over field-based injection (@Autowired) for improved testability.
     */
    public UserService(BookmarkRepo bookmarkRepo,
            UserRepo userRepo,
            JwtUtils jwtUtils,
            PasswordEncoder passwordEncoder) {
        this.bookmarkRepo = bookmarkRepo;
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * persists a new user entity in the database after validating uniqueness.
     */
    public void saveUser(AuthRequest request) {
        try {
            // Uniqueness check: Prevent duplicate registrations early in the lifecycle
            if (userRepo.findByUsername(request.getUsername()).isEmpty()) {
                User user = new User();
                user.setUsername(request.getUsername());

                // Salted hashing: We never store raw passwords. BCrypt handles salting
                // automatically.
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepo.save(user);

                /*
                 * Identity Consolidation: If the user was using the site as a guest,
                 * we move their anonymous bookmarks to their new official account.
                 */
                consolidateIdentity(user, request.getGuestId());
            } else {
                throw new RuntimeException("User registration failed: Identity already exists.");
            }
        } catch (Exception e) {
            // Diagnostic logging for backend monitoring
            e.printStackTrace();
            throw new RuntimeException("Persistence failure during user registration: " + e.getMessage());
        }
    }

    /**
     * Validates credentials and returns a JWT session token.
     */
    public AuthResponse login(AuthRequest request) {
        try {
            // Resolution: Fetch the persisted user by username
            User user = userRepo.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Authentication failed: Principal not found."));

            // Verification: Compare the raw request password against the encoded hash
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // Token Issuance: Generate a stateless JWT for subsequent request authorization
                String token = jwtUtils.generateToken(user.getUsername());

                // Post-Login Synchronization: Ensure any guest session data is migrated to the
                // authenticated principal
                consolidateIdentity(user, request.getGuestId());

                return new AuthResponse(token, user.getUsername());
            } else {
                throw new RuntimeException("Authentication failed: Credential mismatch.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Internal error during authentication lifecycle: " + e.getMessage());
        }
    }

    /**
     * Migrates "Orphaned" bookmarks from a guest session to a registered user
     * account.
     * This method ensures seamless user experience transition from anonymous to
     * authenticated states.
     */
    private void consolidateIdentity(User user, String guestId) {
        // Boundary condition: If no guest session existed, terminate migration early
        if (guestId == null || guestId.isEmpty())
            return;

        // Query: Fetch all bookmarks currently associated with the unique guest
        // identifier
        List<Bookmark> orphans = bookmarkRepo.findByGuestId(guestId);

        // Batch Mapping: Link each orphaned bookmark to the new authenticated user
        orphans.forEach(b -> {
            b.setUser(user);
            b.setGuestId(null); // Remove the guest identifier to finalize the transition
        });

        // Batch Persistence: Flush all updated bookmarks in a single transaction
        bookmarkRepo.saveAll(orphans);
    }
}
