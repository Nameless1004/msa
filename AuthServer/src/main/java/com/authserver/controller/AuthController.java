package com.authserver.controller;

import com.authserver.common.dto.ResponseDto;
import com.authserver.dto.LoginRequest;
import com.authserver.dto.LoginResponse;
import com.authserver.dto.SignupRequest;
import com.authserver.dto.SignupResponse;
import com.authserver.security.AuthUser;
import com.authserver.security.AuthUserDetails;
import com.authserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/signup")
    public ResponseEntity<ResponseDto<SignupResponse>> signup(
            @RequestBody SignupRequest signupRequest) {

        return ResponseDto.of(HttpStatus.OK, authService.signup(signupRequest)).toEntity();
    }

    @GetMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponse>> login(
            @RequestBody LoginRequest loginRequest) {

        return ResponseDto.of(HttpStatus.OK, authService.login(loginRequest)).toEntity();
    }

    @DeleteMapping("/account")
    public ResponseEntity<?> delete(
            @AuthUser AuthUserDetails authUser) {
        authService.deleteAccount(authUser);
        return ResponseDto.of(HttpStatus.OK, "회원탈퇴에 성공하였습니다.").toEntity();
    }
}
