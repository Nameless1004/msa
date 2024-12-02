package com.authserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank
    private final String email;
    @NotBlank
    private final String password;
    private final String adminToken;

    public LoginRequest(String email, String password, String adminToken) {
        this.email = email;
        this.password = password;
        this.adminToken = adminToken;
    }
}
