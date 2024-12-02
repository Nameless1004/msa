package com.authserver.service;

import com.authserver.dto.LoginRequest;
import com.authserver.dto.LoginResponse;
import com.authserver.dto.SignupRequest;
import com.authserver.dto.SignupResponse;
import com.authserver.entity.User;
import com.authserver.enums.TokenType;
import com.authserver.enums.UserRole;
import com.authserver.exceptions.AccessDeniedException;
import com.authserver.exceptions.InvalidRequestException;
import com.authserver.repository.UserRepository;
import com.authserver.security.AuthUserDetails;
import com.authserver.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedissonClient redissonClient;
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

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmailOrElseThrow(request.getEmail());

        if(user.isDeleted()) {
            throw new InvalidRequestException("이미 탈퇴한 회원입니다.");
        }

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidRequestException("비밀번호가 일치하지 않습니다.");
        }

        if(user.getRole() == UserRole.ROLE_ADMIN) {
            if(!StringUtils.hasText(request.getAdminToken()) || !request.getAdminToken().equals(adminToken)) {
                throw new AccessDeniedException();
            }
        }

        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getEmail(), user.getRole());


        redissonClient.getBucket(JwtUtil.REDIS_REFRESH_TOKEN_PREFIX + user.getId()).set(refreshToken, Duration.ofMillis(TokenType.REFRESH.getLifeTime()));
        return new LoginResponse(user, accessToken, refreshToken);
    }

    public void deleteAccount(AuthUserDetails authUser) {
        User user = userRepository.findByIdOrElseThrow(authUser.getId());

        if(user.isDeleted()) {
            throw new InvalidRequestException("이미 탈퇴한 유저입니다.");
        }

        user.delete();
    }
}
