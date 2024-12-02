package com.authserver.dto;

import com.authserver.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {

    @Email
    private final String email;
    @NotBlank
    private final String password;
    @NotBlank
    private final String nickname;
    private final String adminToken;
    private final UserRole role;

    public SignupRequest(String email, String password, UserRole role, String nickname, String adminToken) {
        this.email = email;
        this.password = password;
        this.role = role == null ? UserRole.ROLE_USER : role;
        this.nickname = nickname;
        this.adminToken = adminToken;
    }
}
