package com.example.keuangan.controller;

import com.example.keuangan.util.CookieUtil;
import com.example.keuangan.payload.ApiResponse;
import com.example.keuangan.payload.MessageResponse;
import com.example.keuangan.dto.auth.AuthResponseDto;
import com.example.keuangan.dto.auth.LoginRequestDto;
import com.example.keuangan.dto.auth.RefreshTokenResponseDto;
import com.example.keuangan.dto.auth.RegisterRequestDto;
import com.example.keuangan.dto.user.UserResponseDto;
import com.example.keuangan.service.AuthService;
import com.example.keuangan.service.FileStorageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "Handles user authentication operations")
public class AuthController {

    private final AuthService authService;
    private final FileStorageService fileStorageService;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final long REFRESH_TOKEN_VALIDITY_MS = 86400000L; // 24 jam

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    public ResponseEntity<ApiResponse<AuthResponseDto>> register(@RequestBody @Valid RegisterRequestDto request) {
        AuthResponseDto response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns access token + refresh token in cookie")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody LoginRequestDto request) {
        AuthResponseDto authResponse = authService.login(request);

        String refreshToken = authResponse.getRefreshToken();
        if (refreshToken == null) {
            refreshToken = "";
        }

        ResponseCookie cookie = CookieUtil.createRefreshTokenCookie(
                refreshToken,
                REFRESH_TOKEN_VALIDITY_MS);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success("Login successful", authResponse));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Get new Access Token using Refresh Token from Cookie")
    public ResponseEntity<RefreshTokenResponseDto> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RefreshTokenResponseDto tokenResponse = authService.refresh(refreshToken);

        String newRefreshToken = tokenResponse.getNewRefreshToken();
        if (newRefreshToken == null) {
            newRefreshToken = "";
        }

        ResponseCookie cookie = CookieUtil.createRefreshTokenCookie(
                newRefreshToken,
                tokenResponse.getRefreshTokenExpiry());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(tokenResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Blacklist refresh token and clear cookie")
    public ResponseEntity<MessageResponse> logout(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken) {
        if (refreshToken != null) {
            authService.logout(java.util.Objects.requireNonNull(refreshToken));
        }

        ResponseCookie cookie = CookieUtil.deleteRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Logout success"));
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices", description = "Revokes all refresh tokens for the user")
    public ResponseEntity<MessageResponse> logoutAll(Authentication authentication) {
        authService.logoutAllDevices(authentication.getName());
        return ResponseEntity.ok(new MessageResponse("Logout all devices success"));
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload file", description = "Uploads a file to the server")
    public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("File tidak boleh kosong"));
        }

        try {
            String fileName = fileStorageService.storeFile(file);
            return ResponseEntity.ok(new MessageResponse("Upload berhasil: " + fileName));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Upload gagal: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User Profile", description = "Get details of the currently authenticated user")
    public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUser(Authentication authentication) {
        UserResponseDto userProfile = authService.getUserProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", userProfile));
    }
}
