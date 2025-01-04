package com.demo.auth.auth;

import com.demo.auth.auth.dto.AuthenticationResponse;
import com.demo.auth.core.IT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.MessageFormat;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IT
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private AuthenticationResponse tokens;

    @BeforeAll
    void setUp() {
        tokens = AuthenticationResponse.builder().build();
    }

    @Test
    @Order(1)
    void checkForbiddenRoute() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    void checkWrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content("username=user&password=password")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    void checkForRegister() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"password\"}")
                )
                .andExpect(status().isCreated());
    }

    @Test
    @Order(4)
    void checkForLogin() throws Exception {
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content("username=user&password=password")
                )
                .andExpect(status().isOk())
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    tokens = new ObjectMapper().readValue(responseBody, AuthenticationResponse.class);
                });
    }

    @Test
    @Order(5)
    void checkPrivateRouteAccess() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", MessageFormat.format("Bearer {0}", tokens.getAccessToken())
                        ))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @Order(6)
    void checkForSecondRegister() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"password\"}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void checkForRefreshToken() throws Exception {
        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"refreshToken\":\"%s\"}", tokens.getRefreshToken()))
                )
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    tokens = new ObjectMapper().readValue(responseBody, AuthenticationResponse.class);
                });

        checkPrivateRouteAccess();
    }
}
