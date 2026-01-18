package com.example.keuangan.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String refreshToken;

    public AuthResponseDto(String token) {
        this.token = token;
        this.refreshToken = null;
    }
}
