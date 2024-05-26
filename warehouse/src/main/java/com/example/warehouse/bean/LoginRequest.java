package com.example.warehouse.bean;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String user, String password) {
        this.username = user;
        this.password = password;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}