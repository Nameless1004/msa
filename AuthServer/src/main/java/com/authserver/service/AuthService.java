package com.authserver.service;

import com.authserver.dto.SignupRequest;
import com.authserver.dto.SignupResponse;
import com.authserver.entity.User;
import com.authserver.enums.UserRole;
import com.authserver.exceptions.AccessDeniedException;
import com.authserver.exceptions.InvalidRequestException;
import com.authserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    @Value("${jwt.admin.token}")
    private String adminToken;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(SignupRequest request) {
        if (!request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")) {
            throw new InvalidRequestException("비밀번호는 최소 6자 이상이며 영어와 숫자를 포함해야 합니다.");
        }

        if(request.getRole() == UserRole.ROLE_ADMIN) {
            if( !StringUtils.hasText(request.getAdminToken()) || !request.getAdminToken().equals(adminToken)) {
                throw new AccessDeniedException();
            }
        }

        String password = passwordEncoder.encode(request.getPassword());
        String email = request.getEmail();
        String nickname = request.getNickname();

        if(userRepository.existsByEmail(email)) {
            throw new InvalidRequestException("중복된 이메일입니다.");
        }

        if(userRepository.existsByNickname(email)) {
            throw new InvalidRequestException("중복된 닉네임입니다.");
        }

        User user  = new User(password, email, nickname, request.getRole());
        user = userRepository.save(user);

        return new SignupResponse(user.getId());
    }
}
