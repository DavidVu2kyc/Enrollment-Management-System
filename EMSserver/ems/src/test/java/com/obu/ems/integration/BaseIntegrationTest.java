package com.obu.ems.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.obu.ems.model.User;
import com.obu.ems.repository.UserRepository;
import com.obu.ems.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpHeaders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public abstract class   BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("mysecretpassword");

    static {
            // Add this before any Testcontainer initialization
        // System.setProperty("docker.host", "unix:///Users/David/.docker/run/docker.sock");
        // System.setProperty("DOCKER_HOST", "unix:///Users/David/.docker/run/docker.sock");
        postgres.start();
    }

    // connecting spirngboot and container
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    protected MockMvc getMockMvc() {
        return mockMvc;
    }

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected User createTestUser(String username, String password, User.Role role) {
        User user = User.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .build();
        return userRepository.save(user);
    }

    protected String getJwtToken(String username) {
        return jwtTokenProvider.generateTokenFromUsername(username);
    }

    protected HttpHeaders authenticatedHeaders(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getJwtToken(username));
        return headers;
    }

    //

}
