package com.obu.ems.controller;

import com.obu.ems.dto.*;
import com.obu.ems.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "jwt", required = false) String token, HttpServletResponse response) {

        log.info("Logout attempt");
        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Token cookie missing");
        }

        try {
            authService.logout(token);
            Cookie cookie = new Cookie("jwt", null);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);


            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }
    }

}