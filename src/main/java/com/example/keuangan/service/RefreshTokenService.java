package com.example.keuangan.service;

import com.example.keuangan.entity.RefreshToken;
import com.example.keuangan.entity.User;
import com.example.keuangan.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public RefreshToken createRefreshToken(User user, long durationMs) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                Instant.now().plusMillis(durationMs)
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }

    public void revokeByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    public void revokeAllByUser(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    public RefreshToken verifyAndRotate(String oldToken) {

        RefreshToken token = refreshTokenRepository.findByToken(oldToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }

        User user = token.getUser();

        refreshTokenRepository.delete(token);

        RefreshToken newToken = new RefreshToken();
        newToken.setUser(user);
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiryDate(
                Instant.now().plusMillis(refreshExpiration)
        );

        refreshTokenRepository.save(newToken);
        return newToken;
    }
}