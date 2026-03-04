package com.obu.ems.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SectionIntegrationTest extends BaseIntegrationTest {

    @Test
    void testCreateSection() throws Exception {
        String token = getJwtToken("admin");

        String requestBody = "{\"name\":\"CS101\",\"capacity\":30}";

        getMockMvc().perform(post("/api/sections")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("CS101"))
                .andExpect(jsonPath("$.capacity").value(30));
    }
    
}
