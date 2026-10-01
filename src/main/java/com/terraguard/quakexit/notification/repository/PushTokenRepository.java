package com.terraguard.quakexit.notification.repository;

import com.terraguard.quakexit.notification.entity.PushToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    Optional<PushToken> findByToken(String token);
    Optional<PushToken> findByIdAndUserId(Long id, Long userId);
}
