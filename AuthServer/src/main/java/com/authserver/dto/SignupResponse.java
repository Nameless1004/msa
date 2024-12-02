package com.authserver.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class SignupResponse {
    private final Long userId;

    public SignupResponse(Long userId) {
        this.userId = userId;
    }
}
