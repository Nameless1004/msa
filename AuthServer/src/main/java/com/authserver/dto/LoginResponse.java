package com.authserver.dto;

import com.authserver.entity.User;
import lombok.Getter;

@Getter
public class LoginResponse {

    private final Long id;
    private final String email;
    private final String userNickname;
    private final String accessToken;
    private final String refreshToken;

    public LoginResponse(User user, String accessToken, String refreshToken) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.userNickname = user.getNickname();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
