package com.rydzwr.tictactoe.web.controller.rest;

import com.rydzwr.tictactoe.web.WebTestConfig;
import jakarta.servlet.http.Cookie;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Import(WebTestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserControllerTest {
    static final String LOGIN_PREFIX = "Basic ";
    static final String HEADER_STRING = HttpHeaders.AUTHORIZATION;
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    @Test
    @DisplayName("Should Register New User And Then Allow Him to Login")
    public void registerTest() throws Exception {
        final String[] results = new String[2];

        final String requestBody = "{\"name\":\"test\",\"password\":\"test123\"}";

        // REGISTERING NEW USER TO DB
        this.mockMvc.perform(
                        post("/api/user/register")
                                .servletPath("/api/user/register")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        // LOGGING AFTER SUCCESSFUL REGISTER
        this.mockMvc.perform(
                        get("/api/login")
                                .servletPath("/api/login")
                                .headers(getBasicAuthHeader("test", "test123")))
                .andDo(result -> {
                    var parser = new JSONObject(result.getResponse().getContentAsString());
                    results[0] = parser.getString("access_token");
                    results[1] = parser.getString("role");
                });

        // SAVING USER'S ACCESS TOKEN AND ROLE FROM JSON
        String accessToken = results[0];
        String userRole = results[1];

        // VALIDATING DATA
        assertNotNull(accessToken);
        assertThat(userRole, equalTo("USER"));
    }

    private HttpHeaders getBasicAuthHeader(String name, String password) {
        String valueToEncode = name + ":" + password;
        String encodedValue = Base64.getEncoder().encodeToString(valueToEncode.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_STRING,LOGIN_PREFIX + encodedValue);
        return headers;
    }
}
