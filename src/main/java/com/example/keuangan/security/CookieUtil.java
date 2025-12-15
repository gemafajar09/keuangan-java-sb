package com.example.keuangan.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void addRefreshTokenCookie(
            HttpServletResponse response,
            String token,
            long maxAge,
            boolean secure
    ) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/api/auth");
        cookie.setMaxAge((int) (maxAge / 1000));

        response.addCookie(cookie);
    }

    public static void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}