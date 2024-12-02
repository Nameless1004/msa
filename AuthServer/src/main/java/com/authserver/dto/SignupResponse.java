package com.authserver.dto;

import lombok.Getter;

@Getter
public class SignupResponse {
    private final Long userId;

    public SignupResponse(Long userId) {
        this.userId = userId;
    }
}
