package com.devhoard.controller; // API layer for identity Management

// Data Transfer Objects for authentication payloads
import com.devhoard.DTO.AuthRequest;
import com.devhoard.DTO.AuthResponse;
import com.devhoard.service.UserService;

// standard Spring MVC and Lombok annotations
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for user lifecycle operations: Registration and Authentication.
 * Routes requests to the UserService for business logic and identity consolidation.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost"}) // Permitting traffic from the frontend development servers
public class AuthController {

    private final UserService userService;

    /**
     * Dependency injection via constructor to maintain immutability of the service reference.
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /register: persists a new user account.
     * Triggers identity migration if a guestId is present in the request payload.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        try {
            // Processing user creation and salt-encoded password storage
            userService.saveUser(request);
            return new ResponseEntity<>(HttpStatus.CREATED); // Returns 201 Created on success
        } catch (Exception e) {
            // Returning a generic failure message to avoid leaking internal stack traces or database structure
            return new ResponseEntity<>("Registration failed: Unique identity constraint or persistence error.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST /login: Validates credentials and issues a stateless JWT session token.
     * Success triggers the consolidation of any anonymous guest data into the user profile.
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK) // Explicitly stating 200 OK for successful authentication
    public AuthResponse login(@RequestBody AuthRequest request) {
        // Delegating validation and token generation to the service tier
        return userService.login(request);
    }
}

