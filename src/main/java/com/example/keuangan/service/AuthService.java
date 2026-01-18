package com.example.keuangan.service;

import com.example.keuangan.dto.auth.AuthResponseDto;
import com.example.keuangan.dto.auth.LoginRequestDto;
import com.example.keuangan.dto.auth.RefreshTokenResponseDto;
import com.example.keuangan.dto.auth.RegisterRequestDto;
import com.example.keuangan.entity.Role;
import com.example.keuangan.entity.RefreshToken;
import com.example.keuangan.entity.User;
import com.example.keuangan.repository.RefreshTokenRepository;
import com.example.keuangan.repository.RoleRepository;
import com.example.keuangan.repository.UserRepository;
import com.example.keuangan.util.BaseServiceUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService extends BaseServiceUtil {

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

                Role role;
                if (request.getRoleId() != null) {
                        role = roleRepository.findById(request.getRoleId())
                                        .orElseThrow(() -> new RuntimeException("Role not found"));
                } else {
                        role = roleRepository.findByName("USER")
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Default role 'USER' not found. Database might need seeding."));
                }
                user.setRole(role);

                userRepository.save(user);

                String token = jwtService.generateToken(
                                user.getEmail(),
                                user.getRole().getName());

                return new AuthResponseDto(token, null);
        }

        @Transactional
        public AuthResponseDto login(LoginRequestDto request) {

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

                if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        throw new BadCredentialsException("Invalid credentials");
                }

                String token = jwtService.generateToken(
                                user.getEmail(),
                                user.getRole().getName());

                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, refreshExpiration);

                return new AuthResponseDto(token, refreshToken.getToken());
        }

        @Transactional
        public RefreshTokenResponseDto refresh(@NonNull String refreshToken) {

                RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                                .map(refreshTokenService::verifyExpiration)
                                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

                User user = token.getUser();

                String accessToken = jwtService.generateAccessToken(
                                user.getEmail(),
                                user.getRole().getName());

                RefreshToken result = refreshTokenService.verifyAndRotate(refreshToken);

                RefreshTokenResponseDto response = new RefreshTokenResponseDto(accessToken);
                response.setNewRefreshToken(result.getToken());
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

        public com.example.keuangan.dto.user.UserResponseDto getUserProfile(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return new com.example.keuangan.dto.user.UserResponseDto(
                                user.getId(),
                                user.getEmail(),
                                user.getRole().getName(),
                                user.getFamily() != null ? new com.example.keuangan.dto.family.FamilyDto() {
                                        {
                                                setId(user.getFamily().getId());
                                                setName(user.getFamily().getName());
                                                setCode(user.getFamily().getCode());
                                        }
                                } : null);
        }
}
