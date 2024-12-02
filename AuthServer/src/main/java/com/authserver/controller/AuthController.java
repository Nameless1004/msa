package com.authserver.controller;

import com.authserver.common.dto.ResponseDto;
import com.authserver.dto.SignupRequest;
import com.authserver.dto.SignupResponse;
import com.authserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> login() {

        return ResponseEntity.ok("test!!");
    }
}
