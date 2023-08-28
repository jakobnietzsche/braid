package com.braid.controller;

import com.braid.dto.LoginDto;
import com.braid.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.braid.constants.JwtConstants.LONG_EXPIRATION_TIME;
import static com.braid.constants.JwtConstants.SHORT_EXPIRATION_TIME;

@RestController
@RequestMapping("/accounts")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateToken(authentication, loginRequest.getStayLoggedIn());
        long expirationTime = loginRequest.getStayLoggedIn() ? LONG_EXPIRATION_TIME : SHORT_EXPIRATION_TIME;
        Cookie jwtCookie = new Cookie("JWT", jwt);
        jwtCookie.setMaxAge((int) expirationTime);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");

        response.addCookie(jwtCookie);
        response.addHeader("Set-Cookie", jwtCookie+ "; SameSite=None; Secure");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuthentication(HttpServletRequest request) {
        boolean isAuthenticated = false;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JWT")) {
                    String token = cookie.getValue();
                    isAuthenticated = jwtService.validateToken(token);
                }
            }
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("isAuthenticated", isAuthenticated);
        return ResponseEntity.ok(responseBody);
    }
}
