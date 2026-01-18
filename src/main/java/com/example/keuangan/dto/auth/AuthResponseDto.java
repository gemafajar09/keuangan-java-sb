package com.example.keuangan.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
        this.refreshToken = null;
    }
}
