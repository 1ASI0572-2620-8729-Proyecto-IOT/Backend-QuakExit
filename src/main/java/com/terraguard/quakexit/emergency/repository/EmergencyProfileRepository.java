package com.terraguard.quakexit.emergency.repository;

import com.terraguard.quakexit.emergency.entity.EmergencyProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyProfileRepository extends JpaRepository<EmergencyProfile, Long> {
    Optional<EmergencyProfile> findByUserId(Long userId);
}
