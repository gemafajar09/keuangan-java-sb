package com.example.keuangan.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenResponseDto {
    private String accessToken;
    private String newRefreshToken;
    private long refreshTokenExpiry;

    public RefreshTokenResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setNewRefreshToken(String newRefreshToken) {
        this.newRefreshToken = newRefreshToken;
    }

    public void setRefreshTokenExpiry(long refreshTokenExpiry) {
        this.refreshTokenExpiry = refreshTokenExpiry;
    }
}
