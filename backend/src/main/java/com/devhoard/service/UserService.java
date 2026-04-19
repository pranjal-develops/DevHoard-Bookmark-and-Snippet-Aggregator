package com.devhoard.service;

import com.devhoard.DTO.AuthRequest;
import com.devhoard.DTO.AuthResponse;
import com.devhoard.entities.Bookmark;
import com.devhoard.entities.User;
import com.devhoard.repository.BookmarkRepo;
import com.devhoard.repository.UserRepo;
import com.devhoard.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
// @RequiredArgsConstructor
public class UserService {

    private final BookmarkRepo bookmarkRepo;
    private final UserRepo userRepo;
    private final JwtUtils jwtUtils;
    // private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    // This is not needed since we are creating passwordEncoder as a bean in
    // SecurityConfig class
    private final PasswordEncoder passwordEncoder;

    public UserService(BookmarkRepo bookmarkRepo,
            UserRepo userRepo,
            JwtUtils jwtUtils,
            PasswordEncoder passwordEncoder) {
        this.bookmarkRepo = bookmarkRepo;
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUser(AuthRequest request) {
        try {
            if (userRepo.findByUsername(request.getUsername()).isEmpty()) {
                User user = new User();
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepo.save(user);
                consolidateIdentity(user, request.getGuestId());
            } else
                throw new RuntimeException("User already exists");
        } catch (Exception e) {
            e.printStackTrace(); // 🕵️‍♂️ LOUD ERROR
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    public AuthResponse login(AuthRequest request) {
        try {
            User user = userRepo.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtUtils.generateToken(user.getUsername());
                consolidateIdentity(user, request.getGuestId());
                return new AuthResponse(token, user.getUsername());
            } else {
                throw new RuntimeException("Invalid credentials");
            }
        } catch (Exception e) {
            e.printStackTrace(); // 🕵️‍♂️ LOUD ERROR
            throw new RuntimeException("Failed to login: " + e.getMessage());
        }
    }

    private void consolidateIdentity(User user, String guestId) {
        if (guestId == null || guestId.isEmpty())
            return;

        List<Bookmark> orphans = bookmarkRepo.findByGuestId(guestId);
        orphans.forEach(b -> {
            b.setUser(user);
            b.setGuestId(null);
        });
        bookmarkRepo.saveAll(orphans);
    }

}
