package com.example.keuangan.service;

import com.example.keuangan.dto.AuthResponseDto;
import com.example.keuangan.dto.LoginRequestDto;
import com.example.keuangan.dto.RefreshTokenResponseDto;
import com.example.keuangan.dto.RegisterRequestDto;
import com.example.keuangan.entity.Role;
import com.example.keuangan.entity.RefreshToken;
import com.example.keuangan.entity.User;
import com.example.keuangan.repository.RefreshTokenRepository;
import com.example.keuangan.repository.RoleRepository;
import com.example.keuangan.repository.UserRepository;
import com.example.keuangan.util.BaseService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService extends BaseService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public AuthResponseDto register(RegisterRequestDto request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Default role is USER (ID 1) if not specified or found
        Long roleId = request.getRoleId() != null ? request.getRoleId() : 1L;
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);

        userRepository.save(user);

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().getName());

        return new AuthResponseDto(token, null);
    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) { // Removed HttpServletResponse

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().getName());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, refreshExpiration);

        // We return the refresh token string in the response object as well (optional,
        // but requested by some clients)
        // ideally it's only in the cookie.

        return new AuthResponseDto(token, refreshToken.getToken());
    }

    @Transactional
    public RefreshTokenResponseDto refresh(@NonNull String refreshToken) {
        // Removed HttpServletResponse arg

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        User user = token.getUser();

        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                user.getRole().getName());

        RefreshToken result = refreshTokenService.verifyAndRotate(refreshToken);

        // Return new access token and the result for cookie creation in Controller
        RefreshTokenResponseDto response = new RefreshTokenResponseDto(accessToken);
        response.setNewRefreshToken(result.getToken()); // Need to ensure DTO has this or handle in Controller
        response.setRefreshTokenExpiry(result.getExpiryDate().toEpochMilli());

        return response;
    }

    @Transactional
    public void logout(@NonNull String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    @Transactional
    public void logoutAllDevices(@NonNull String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenService.revokeAllByUser(user.getId());
    }

}
