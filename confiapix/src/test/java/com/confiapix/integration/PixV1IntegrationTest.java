package com.confiapix.integration;

import com.confiapix.support.IntegrationTestSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PixV1IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListPixForAuthenticatedTenant() throws Exception {
        IntegrationTestSupport.AuthTokens auth = IntegrationTestSupport.registerAdmin(mockMvc, objectMapper, "pix-list");

        MvcResult result = mockMvc.perform(get("/api/v1/pix")
                        .header("Authorization", "Bearer " + auth.token())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = IntegrationTestSupport.readJson(mockMvc, objectMapper, result);
        assertThat(response.get("success").asBoolean()).isTrue();
        assertThat(response.get("data").get("content").isArray()).isTrue();
    }
}
