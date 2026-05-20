package com.dto;

// AuthRequest.java
public class AuthRequest {
    private String email;
    private String password;

    // Getters y Setters (Importante para que Spring pueda leer los datos)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}