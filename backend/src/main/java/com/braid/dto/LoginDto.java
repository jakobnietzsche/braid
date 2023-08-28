package com.braid.dto;

public class LoginDto {
    private String username;
    private String password;

    private boolean stayLoggedIn;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean getStayLoggedIn() { return stayLoggedIn; }
}
