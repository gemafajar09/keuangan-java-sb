package com.example.keuangan.scheduler;

import com.example.keuangan.entity.User;
import com.example.keuangan.repository.RefreshTokenRepository;
import com.example.keuangan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InactivityLogoutScheduler {

    private static final Logger log = LoggerFactory.getLogger(InactivityLogoutScheduler.class);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${user.inactivity-timeout:900000}")
    private long inactivityTimeout;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void checkInactiveUsers() {
        log.debug("Running inactivity check for users...");

        Instant now = Instant.now();
        Instant cutoffTime = now.minusMillis(inactivityTimeout);

        List<User> inactiveUsers = userRepository.findByIsOnlineTrueAndLastActivityAtBefore(cutoffTime);

        if (!inactiveUsers.isEmpty()) {
            log.info("Found {} inactive users to logout", inactiveUsers.size());

            for (User user : inactiveUsers) {
                log.info("Auto-logout user: {} due to inactivity", user.getEmail());

                user.setIsOnline(false);
                userRepository.save(user);

                refreshTokenRepository.deleteByUserId(user.getId());
            }

            log.info("Auto-logout completed for {} users", inactiveUsers.size());
        }

        List<User> activeUsers = userRepository.findByIsOnlineTrueAndLastActivityAtAfter(cutoffTime);

        if (!activeUsers.isEmpty()) {
            log.debug("Found {} active users to extend session", activeUsers.size());

            Instant extendedExpiry = now.plusMillis(900000);

            for (User user : activeUsers) {
                refreshTokenRepository.findByUserId(user.getId()).ifPresent(token -> {
                    token.setExpiryDate(extendedExpiry);
                    refreshTokenRepository.save(token);
                    log.debug("Extended session for user: {}", user.getEmail());
                });
            }
        }
    }
}
