package com.confiapix.support;

import com.confiapix.presentation.request.LoginRequest;
import com.confiapix.presentation.request.RegisterRequest;
import com.confiapix.presentation.request.StoneCredentialsRequest;
import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.AuthResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class IntegrationTestSupport {

    private IntegrationTestSupport() {
    }

    public static AuthTokens registerAdmin(MockMvc mockMvc, ObjectMapper objectMapper, String suffix) throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setTenantName("Tenant " + suffix);
        register.setName("Admin " + suffix);
        register.setEmail("admin-" + suffix + "@confiapix.test");
        register.setPassword("secret123");

        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andReturn();

        ApiResponse<AuthResponse> registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        return new AuthTokens(registerResponse.getData().getToken(), registerResponse.getData().getTenantId());
    }

    public static String login(MockMvc mockMvc, ObjectMapper objectMapper, String email) throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword("secret123");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<AuthResponse> loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        return loginResponse.getData().getToken();
    }

    public static void saveStoneCredentials(MockMvc mockMvc, ObjectMapper objectMapper, String token, String accountId)
            throws Exception {
        StoneCredentialsRequest request = new StoneCredentialsRequest();
        request.setClientId("client-test");
        request.setClientSecret("secret-test");
        request.setAccountId(accountId);
        request.setMerchantId(accountId);

        mockMvc.perform(put("/api/v1/integrations/stone/credentials")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    public static JsonNode readJson(MockMvc mockMvc, ObjectMapper objectMapper, MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    public record AuthTokens(String token, java.util.UUID tenantId) {
    }
}
