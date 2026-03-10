package com.obu.ems.service;

import com.obu.ems.dto.*;
import com.obu.ems.exception.ConflictException;
import com.obu.ems.exception.ResourceNotFoundException;
import com.obu.ems.model.Degree;
import com.obu.ems.model.Student;
import com.obu.ems.model.User;
import com.obu.ems.repository.DegreeRepository;
import com.obu.ems.repository.StudentRepository;
import com.obu.ems.repository.UserRepository;
import com.obu.ems.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final DegreeRepository degreeRepository;

    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();


    // login ( authenticate user and receive JWT token )
    public AuthResponse login(LoginRequest request) {

        // use authentication manager to authenticate username and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        // generate JWT token
        String jwtToken = jwtTokenProvider.generateToken(authentication);

        // extract the role from the authenticated user
        String role = authentication.getAuthorities()
                .stream()
                .findFirst().map(GrantedAuthority::getAuthority)
                .orElse("STUDENT");

        AuthResponse response = AuthResponse.builder()
                .token(jwtToken)
                .username(request.getUsername())
                .role(role)
                .build();

        return response;

    }

    // register new account - a new pròile ( admin only )
    public StudentResponse register(RegisterRequest request) {
        // validate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("username already taken");
        }

        // validate student number uniqueness
        if (studentRepository.existsByStudentNumber(request.getStudentNumber())) {
            throw new ConflictException("student number already taken");
        }

        Degree degree = degreeRepository.findById(request.getDegreeId())
                .orElseThrow(() -> new ResourceNotFoundException("invalid degree id"));

        // create new user entity
        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.STUDENT)
                .build();

        userRepository.save(user);

        // create new student profile
        Student student = Student.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .studentNumber(request.getStudentNumber())
                .degree(degree)
                .user(user)
                .build();

        studentRepository.save(student);

        DegreeResponse degreeResponse = DegreeResponse.builder()
                .degreeId(degree.getDegreeId())
                .name(degree.getName())
                .build();

        return StudentResponse.builder()
                .studentId(student.getStudentId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .studentNumber(student.getStudentNumber())
                .degree(degreeResponse)
                .build();
    }

    public void logout(String token) {

        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        // Calculate the token's remaining TTL and add to the blacklist
        long expiration = jwtTokenProvider.getExpirationFromToken(token);
        long timeToLive = expiration - System.currentTimeMillis();
        blacklist.put(token, System.currentTimeMillis() + timeToLive);

    }

    public boolean isBlacklisted(String token) {
        Long expiryTime = blacklist.get(token);
        if (expiryTime == null)
            return false;

        // Remove expired entries
        if (expiryTime < System.currentTimeMillis()) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }


}