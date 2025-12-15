package com.example.keuangan.controller;

import com.example.keuangan.dto.AuthResponse;
import com.example.keuangan.dto.LoginRequest;
import com.example.keuangan.dto.LogoutRequest;
import com.example.keuangan.dto.RefreshTokenRequest;
import com.example.keuangan.dto.RefreshTokenResponse;
import com.example.keuangan.dto.RegisterRequest;
import com.example.keuangan.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(
            @RequestBody RefreshTokenRequest request
    ) {
        return authService.refresh(request.getRefreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Logout success");
    }

    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(Authentication authentication) {
        authService.logoutAllDevices(authentication.getName());
        return ResponseEntity.ok("Logout all devices success");
    }
}
