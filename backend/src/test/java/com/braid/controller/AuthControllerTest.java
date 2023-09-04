package com.braid.controller;

import com.braid.configuration.SecurityConfiguration;
import com.braid.dto.LoginDto;
import com.braid.service.BraidDBUserDetailsService;
import com.braid.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(AuthController.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = AuthController.class)
@ContextConfiguration(classes = SecurityConfiguration.class)
public class AuthControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private BraidDBUserDetailsService braidDBUserDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void login_ValidCredentials_ReturnsOk() throws Exception {
        LoginDto loginRequest = new LoginDto();
        loginRequest.setUsername("username");
        loginRequest.setPassword("password");
        loginRequest.setStayLoggedIn(false);

        String inputJson = new ObjectMapper().writeValueAsString(loginRequest);

        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(jwtService.generateToken(any(), anyBoolean())).thenReturn("someToken");

        MockHttpServletRequestBuilder requestBuilder = post("/accounts/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    public void checkAuthentication_AuthenticCredentials_ReturnsIsAuthenticatedTrue() throws Exception {
        Cookie mockCookie = new Cookie("JWT", "mockJwtToken");

        when(jwtService.validateToken(anyString())).thenReturn(true);

        MockHttpServletRequestBuilder requestBuilder = get("/accounts/check-auth")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(mockCookie);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAuthenticated").value(true));
    }
}
