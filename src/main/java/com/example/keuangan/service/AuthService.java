package com.example.keuangan.service;

import com.example.keuangan.config.CookieUtil;
import com.example.keuangan.dto.AuthResponse;
import com.example.keuangan.dto.LoginRequest;
import com.example.keuangan.dto.RefreshTokenResponse;
import com.example.keuangan.dto.RegisterRequest;
import com.example.keuangan.entity.RefreshToken;
import com.example.keuangan.entity.Role;
import com.example.keuangan.entity.User;
import com.example.keuangan.repository.RefreshTokenRepository;
import com.example.keuangan.repository.UserRepository;
import com.example.keuangan.util.BaseService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService extends BaseService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public AuthResponse register(RegisterRequest request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);

        String token = jwtService.generateToken(
            user.getEmail(),
            user.getRole().name()
        );

        return new AuthResponse(token, null);
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(
            user.getEmail(),
            user.getRole().name()
        );

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user, refreshExpiration);

        CookieUtil.addRefreshTokenCookie(
            response,
            refreshToken.getToken(),
            refreshToken.getExpiryDate().toEpochMilli(),
            false
        );

        info("User {} logged in", user.getEmail());

        return new AuthResponse(token, refreshToken.getToken());
    }

    @Transactional
    public RefreshTokenResponse refresh(
        String refreshToken,
        HttpServletResponse response
    ) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        User user = token.getUser();

        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                user.getRole().name()
        );

        RefreshToken result = refreshTokenService.verifyAndRotate(refreshToken);

        CookieUtil.addRefreshTokenCookie(
            response,
            result.getToken(),
            result.getRefreshTokenExpiry(),
            false
        );

        return new RefreshTokenResponse(accessToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    @Transactional
    public void logoutAllDevices(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenService.revokeAllByUser(user.getId());
    }

}
