package com.authserver.security;

import com.authserver.enums.UserRole;
import lombok.*;

@Getter
@AllArgsConstructor
public class AuthUserDetails {
    private final Long id;
    private final String email;
    private final UserRole role;
}
