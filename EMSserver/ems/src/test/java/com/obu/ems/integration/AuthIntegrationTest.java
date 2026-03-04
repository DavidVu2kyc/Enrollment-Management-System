package com.obu.ems.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.obu.ems.dto.LoginRequest;
import com.obu.ems.dto.RegisterRequest;
import com.obu.ems.model.Degree;
import com.obu.ems.model.User;
import com.obu.ems.repository.DegreeRepository;
import com.obu.ems.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DegreeRepository degreeRepository;

    private Long degreeId;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        userRepository.deleteAll();
        degreeRepository.deleteAll();

        Degree degree = degreeRepository.save(Degree.builder().name("BSCS").build());
        degreeId = degree.getDegreeId();
    }

    // student reistration case
    @Test
    void testSuccessfulStudentRegistration() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("newstudent")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .studentNumber("S202")
                .degreeId(degreeId)
                .build();

        getMockMvc().perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("newstudent")))
                .andExpect(jsonPath("$.studentNumber", is("S202")));
    }

    // username conflict checks ,login success ,and invalid credentials 
    @Test
    void testPreventDuplicateUsername() throws Exception {
        createTestUser("existinguser", "password", User.Role.STUDENT);

        RegisterRequest request = RegisterRequest.builder()
                .username("existinguser")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .studentNumber("S203")
                .degreeId(degreeId)
                .build();

        getMockMvc().perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // login success and invalid credentials 
    @Test
    void testSuccessfulLogin() throws Exception {
        createTestUser("testuser", "password", User.Role.STUDENT);

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        getMockMvc().perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("STUDENT")));
    }

    @Test
    void testInvalidCredentialsLogin() throws Exception {
        createTestUser("testuser", "password", User.Role.STUDENT);

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        getMockMvc().perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
