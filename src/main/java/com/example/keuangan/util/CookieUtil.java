package com.example.keuangan.util;

import org.springframework.http.ResponseCookie;

public class CookieUtil {

    public static ResponseCookie createRefreshTokenCookie(@org.springframework.lang.NonNull String token, long maxAge) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .maxAge(maxAge / 1000)
                .sameSite("Strict")
                .build();
    }

    public static ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .path("/api/auth")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}