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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginDto loginRequest = new LoginDto();
        loginRequest.setUsername("nonExistentUsername");
        loginRequest.setPassword("nonExistentPassword");
        loginRequest.setStayLoggedIn(false);

        String inputJson = new ObjectMapper().writeValueAsString(loginRequest);

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        MockHttpServletRequestBuilder requestBuilder = post("/accounts/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Authorization failed"));
    }

    @Test
    public void logout_SuccessfulLogout_SetsCookieHeader() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/accounts/logout")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"))
                .andReturn();

        String setCookieHeader = result.getResponse().getHeader("Set-Cookie");
        assertEquals("JWT=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Secure; HttpOnly; SameSite=None", setCookieHeader);
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

    @Test
    public void checkAuthentication_InvalidJWT_ReturnsIsAuthenticatedFalse() throws Exception {
        Cookie mockCookie = new Cookie("JWT", "invalidMockJwtToken");

        when(jwtService.validateToken(anyString())).thenReturn(false);

        MockHttpServletRequestBuilder requestBuilder = get("/accounts/check-auth")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(mockCookie);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAuthenticated").value(false));
    }
}
