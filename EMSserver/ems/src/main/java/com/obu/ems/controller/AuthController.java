package com.obu.ems.controller;

import com.obu.ems.dto.*;
import com.obu.ems.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        log.info("Login attempt for username: {}", request.getUsername());

        try {
            AuthResponse response = authService.login(request);
            log.info("Login successful for username: {}", request.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Login failed for username: {}", request.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<StudentResponse> register(@Valid @RequestBody RegisterRequest request) {

        log.info("Registration attempt for username: {}", request.getUsername());

        StudentResponse response = authService.register(request);

        log.info("User registered successfully: {}", response.getUsername());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}