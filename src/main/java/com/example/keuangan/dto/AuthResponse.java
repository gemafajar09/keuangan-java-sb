package com.example.keuangan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    
    public AuthResponse(String token) {
        this.token = token;
        this.refreshToken = null;
    }
}

