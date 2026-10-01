package com.terraguard.quakexit.emergency.service;

import com.terraguard.quakexit.common.enums.DomainEnums.*;
import com.terraguard.quakexit.common.exception.ApiExceptions.*;
import com.terraguard.quakexit.device.entity.Device;
import com.terraguard.quakexit.device.repository.DeviceRepository;
import com.terraguard.quakexit.emergency.dto.EmergencyDtos.*;
import com.terraguard.quakexit.emergency.entity.*;
import com.terraguard.quakexit.emergency.repository.*;
import com.terraguard.quakexit.iam.entity.User;
import java.time.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Slf4j
public class SeismicService {
    private final DeviceRepository devices; private final SeismicReadingRepository readings; private final SeismicEventRepository events; private final NotificationService notifications;
    @Value("${app.seismic.consecutive-readings:3}") private int requiredReadings;
    @Value("${app.seismic.window-seconds:15}") private long windowSeconds;
    @Transactional public EventResponse process(ReadingRequest request) {
        Device device=devices.findByDeviceCode(request.deviceCode()).orElseThrow(() -> new ResourceNotFoundException("Dispositivo no encontrado"));
        double magnitude=Math.sqrt(request.ax()*request.ax()+request.ay()*request.ay()+request.az()*request.az()); double pga=Math.max(0,magnitude-1.0);
        device.setLastSeenAt(request.timestamp()); device.setBatteryPercentage(request.battery()); device.setStatus(DeviceStatus.ONLINE); devices.save(device);
        readings.save(SeismicReading.builder().device(device).timestamp(request.timestamp()).accelerationX(request.ax()).accelerationY(request.ay()).accelerationZ(request.az()).magnitudeEstimated(pga).frequencyHz(request.freqHz()).build());
        var recent=readings.findByDeviceIdAndTimestampAfterOrderByTimestampDesc(device.getId(), request.timestamp().minusSeconds(windowSeconds)); long confirmations=recent.stream().filter(r -> r.getMagnitudeEstimated() >= 0.02).count();
        log.info("Lectura {} pga={} confirmaciones={}/{}", device.getDeviceCode(), pga, confirmations, requiredReadings);
        var active=events.findFirstByDeviceIdAndStatusOrderByDetectedAtDesc(device.getId(), EventStatus.ACTIVE);
        if (pga >= 0.02 && confirmations >= requiredReadings && active.isEmpty()) {
            Severity severity=severity(pga); device.setLockStatus(LockStatus.UNLOCKED); device.setLightStatus(LightStatus.ON); device.setStatus(DeviceStatus.ALERT); device.setComponentUpdatedAt(Instant.now()); devices.save(device);
            SeismicEvent event=events.save(SeismicEvent.builder().device(device).building(device.getBuilding()).status(EventStatus.ACTIVE).severity(severity).peakAcceleration(pga).detectedAt(request.timestamp()).doorsUnlockedAt(Instant.now()).build()); event.setSmsNotificationsSent(notifications.notifyEmergency(event)); return toResponse(event);
        }
        return active.map(this::toResponse).orElse(null);
    }
    @Transactional public EventResponse falseAlarm(Long eventId, String reason, User user) { SeismicEvent event=events.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado")); if(event.getDevice().getOwner()==null || !event.getDevice().getOwner().getId().equals(user.getId())) throw new org.springframework.security.access.AccessDeniedException("Evento ajeno"); if(event.getStatus()!=EventStatus.ACTIVE && event.getStatus()!=EventStatus.DETECTED) throw new BusinessRuleException("El evento no puede cancelarse"); event.setStatus(EventStatus.FALSE_ALARM); event.setFalseAlarmReason(reason); event.setCancelledBy(user); event.setResolvedAt(Instant.now()); event.getDevice().setLockStatus(LockStatus.LOCKED); event.getDevice().setLightStatus(LightStatus.OFF); devices.save(event.getDevice()); return toResponse(events.save(event)); }
    private Severity severity(double pga) { if(pga>=.5)return Severity.CRITICAL; if(pga>=.25)return Severity.HIGH; if(pga>=.1)return Severity.MODERATE; return Severity.LOW; }
    private EventResponse toResponse(SeismicEvent e) { return new EventResponse(e.getId(),e.getDevice().getId(),e.getStatus(),e.getSeverity(),e.getPeakAcceleration(),e.getDetectedAt(),e.getResolvedAt(),e.getSmsNotificationsSent()); }
}
