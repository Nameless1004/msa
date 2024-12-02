package com.authserver.security;

import com.authserver.enums.UserRole;
import lombok.*;

@Getter
@AllArgsConstructor
public class AuthUserDetails {
    private final Long userId;
    private final String email;
    private final UserRole role;
}
