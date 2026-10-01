package com.terraguard.quakexit.emergency.service;

import com.terraguard.quakexit.emergency.entity.SeismicEvent;
import com.terraguard.quakexit.emergency.repository.EmergencyContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor @Slf4j
public class MockSmsService implements NotificationService {
    private final EmergencyContactRepository contacts;
    @Override public int notifyEmergency(SeismicEvent event) {
        if (event.getDevice().getOwner() == null) return 0;
        var eligible = contacts.findByUserIdOrderByPriorityAsc(event.getDevice().getOwner().getId()).stream().filter(c -> c.isNotifyBySms()).toList();
        eligible.forEach(c -> log.info("SMS simulado a {} para evento {}", c.getPhoneNumber(), event.getId()));
        return eligible.size();
    }
}
