package com.example.keuangan.security;

import com.example.keuangan.service.JwtService;
import com.example.keuangan.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final com.example.keuangan.repository.UserRepository userRepository;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-threshold:300000}")
    private long refreshThreshold;

    @Value("${user.inactivity-timeout:900000}")
    private long inactivityTimeout;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwt = null;
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String email;

        try {
            email = jwtService.extractEmail(jwt);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(java.util.Objects.requireNonNull(email));

            if (jwtService.isTokenValid(jwt)) {

                String role = jwtService.extractRole(jwt);

                com.example.keuangan.entity.User user = userRepository.findByEmail(email).orElse(null);

                if (user != null && Boolean.FALSE.equals(user.getIsOnline())) {
                    filterChain.doFilter(request, response);
                    return;
                }

                if (user != null && user.getLastActivityAt() != null) {
                    Instant now = Instant.now();
                    long inactiveDuration = now.toEpochMilli() - user.getLastActivityAt().toEpochMilli();

                    if (inactiveDuration > inactivityTimeout) {
                        user.setIsOnline(false);
                        userRepository.save(user);

                        Cookie accessCookie = new Cookie("access_token", "");
                        accessCookie.setPath("/");
                        accessCookie.setMaxAge(0);
                        response.addCookie(accessCookie);

                        filterChain.doFilter(request, response);
                        return;
                    }
                }

                if (user != null) {
                    user.setLastActivityAt(Instant.now());
                    userRepository.save(user);
                }

                long tokenRemainingTime = jwtService.getTokenRemainingTime(jwt);
                if (tokenRemainingTime > 0 && tokenRemainingTime < refreshThreshold) {
                    String newAccessToken = jwtService.generateAccessToken(email, role);

                    org.springframework.http.ResponseCookie newAccessCookie = CookieUtil.createAccessTokenCookie(
                            newAccessToken,
                            accessExpiration);
                    response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, newAccessCookie.toString());
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
