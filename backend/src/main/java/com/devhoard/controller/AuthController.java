package com.devhoard.controller;

import com.devhoard.DTO.AuthRequest;
import com.devhoard.DTO.AuthResponse;
import com.devhoard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
// @RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost"})
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        try {
            userService.saveUser(request);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            // throw new RuntimeException(e);
            return new ResponseEntity<>("Registration failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@RequestBody AuthRequest request) {
        return userService.login(request);
    }

}
