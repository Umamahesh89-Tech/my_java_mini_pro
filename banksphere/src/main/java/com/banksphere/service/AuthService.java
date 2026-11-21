package com.banksphere.service;

import com.banksphere.dto.AuthResponse;
import com.banksphere.dto.LoginRequest;
import com.banksphere.dto.RegisterRequest;
import com.banksphere.entity.Role;
import com.banksphere.entity.User;
import com.banksphere.exception.BadRequestException;
import com.banksphere.exception.ConflictException;
import com.banksphere.repository.UserRepository;
import com.banksphere.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new BadRequestException("Invalid username or password");
        }
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));
        return new AuthResponse(jwtUtil.generateToken(user.getUsername()), user.getUsername(), user.getRole().name());
    }
}
