package com.devhoard.service;

import com.devhoard.DTO.AuthRequest;
import com.devhoard.DTO.AuthResponse;
import com.devhoard.entities.User;
import com.devhoard.repository.UserRepo;
import com.devhoard.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final JwtUtils jwtUtils;
//    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();   This is not needed since we are creating passwordEncoder as a bean in SecurityConfig class
    private final PasswordEncoder passwordEncoder;

    public void saveUser(AuthRequest request){
        try {
            if(userRepo.findByUsername(request.getUsername()).isEmpty()) {
                User user = new User();
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepo.save(user);
            } else throw new RuntimeException("User already exists");
        }catch (Exception e){ throw new RuntimeException("Failed to create user");}
    }

    public AuthResponse login(AuthRequest request){
        try{
            User user = userRepo.findByUsername(request.getUsername())
                            .orElseThrow(()-> new RuntimeException("User not found"));
            if(passwordEncoder.matches(request.getPassword(), user.getPassword())){
                String token = jwtUtils.generateToken(user.getUsername());
                return new AuthResponse(token, user.getUsername());
            }else {
                throw new RuntimeException("Invalid credentials");
            }
        }catch (Exception e){
            throw new RuntimeException("Failed to login");
        }
    }

}
