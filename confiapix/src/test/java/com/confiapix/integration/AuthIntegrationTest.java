package com.confiapix.integration;

import com.confiapix.presentation.request.RefreshTokenRequest;
import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.AuthResponse;
import com.confiapix.support.IntegrationTestSupport;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterLoginAndRefreshToken() throws Exception {
        IntegrationTestSupport.AuthTokens tokens = IntegrationTestSupport.registerAdmin(mockMvc, objectMapper, "auth-flow");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin-auth-flow@confiapix.test","password":"secret123"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<AuthResponse> loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertThat(loginResponse.isSuccess()).isTrue();
        assertThat(loginResponse.getData().getToken()).isNotBlank();
        assertThat(loginResponse.getData().getRefreshToken()).isNotBlank();

        RefreshTokenRequest refresh = new RefreshTokenRequest();
        refresh.setRefreshToken(loginResponse.getData().getRefreshToken());

        MvcResult refreshResult = mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refresh)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<AuthResponse> refreshResponse = objectMapper.readValue(
                refreshResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertThat(refreshResponse.getData().getToken()).isNotBlank();
        assertThat(tokens.tenantId()).isEqualTo(loginResponse.getData().getTenantId());
    }
}
