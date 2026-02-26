package com.obu.ems.controller;

import com.obu.ems.dto.*;
import com.obu.ems.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // private final AuthService authService;

    // @PostMapping("/login")
    // public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    //     return ResponseEntity.ok(authService.login(request));
    // }

    // @PostMapping("/register")
    // public ResponseEntity<StudentResponse> register(@Valid @RequestBody RegisterRequest request) {
    //     return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    // }
}
