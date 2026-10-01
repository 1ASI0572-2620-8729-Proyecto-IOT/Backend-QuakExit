package com.terraguard.quakexit.emergency.repository;

import com.terraguard.quakexit.emergency.entity.EmergencyContact;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    List<EmergencyContact> findByUserIdOrderByPriorityAsc(Long userId);
    long countByUserId(Long userId);
    boolean existsByUserIdAndPhoneNumber(Long userId, String phoneNumber);
}
