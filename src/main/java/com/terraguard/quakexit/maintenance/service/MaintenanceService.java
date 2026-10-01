package com.terraguard.quakexit.maintenance.service;

import com.terraguard.quakexit.common.exception.ApiExceptions.*;
import com.terraguard.quakexit.device.entity.Device;
import com.terraguard.quakexit.device.repository.DeviceRepository;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.maintenance.dto.MaintenanceDtos.*;
import com.terraguard.quakexit.maintenance.entity.MaintenanceAlert;
import com.terraguard.quakexit.maintenance.repository.MaintenanceAlertRepository;
import com.terraguard.quakexit.notification.service.NotificationManager;
import java.time.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class MaintenanceService {
    private final MaintenanceAlertRepository alerts;
    private final DeviceRepository devices;
    private final NotificationManager notifications;
    @Value("${app.maintenance.low-battery-threshold:20}") private int batteryThreshold;
    @Value("${app.maintenance.offline-hours:24}") private long offlineHours;

    @Transactional(readOnly = true)
    public Page<MaintenanceResponse> list(String status, String type, Long deviceId, Long buildingId, Pageable pageable) {
        Specification<MaintenanceAlert> spec = Specification.where(equal("status", status)).and(equal("type", type)).and(equal("device.id", deviceId)).and(equal("buildingId", buildingId));
        return alerts.findAll(spec, pageable).map(this::response);
    }

    @Transactional
    public MaintenanceResponse acknowledge(Long id, User user) {
        MaintenanceAlert alert = find(id); if (alert.getStatus().equals("RESOLVED")) throw new BusinessRuleException("La alerta ya esta resuelta");
        alert.setStatus("ACKNOWLEDGED"); alert.setAcknowledgedBy(user); alert.setAcknowledgedAt(Instant.now()); return response(alerts.save(alert));
    }

    @Transactional
    public MaintenanceResponse resolve(Long id, ResolutionRequest request, User user) {
        MaintenanceAlert alert = find(id); if (alert.getStatus().equals("RESOLVED")) throw new BusinessRuleException("La alerta ya esta resuelta");
        alert.setStatus("RESOLVED"); alert.setResolvedBy(user); alert.setResolvedAt(Instant.now()); alert.setResolutionNote(request == null ? null : request.resolutionNote()); return response(alerts.save(alert));
    }

    @Scheduled(fixedDelayString = "${app.maintenance.scan-interval-ms:300000}")
    @Transactional
    public void scan() {
        Instant offlineSince = Instant.now().minus(Duration.ofHours(offlineHours));
        for (Device device : devices.findAll()) {
            if (device.getBatteryPercentage() != null && device.getBatteryPercentage() <= batteryThreshold) createIfMissing(device, "LOW_BATTERY", device.getBatteryPercentage(), batteryThreshold, "Bateria por debajo del umbral configurado");
            if (device.getLastSeenAt() == null || device.getLastSeenAt().isBefore(offlineSince)) createIfMissing(device, "DEVICE_OFFLINE", device.getLastSeenAt() == null ? null : 0, 0, "Dispositivo sin conexion durante el periodo configurado");
        }
    }

    private void createIfMissing(Device device, String type, Integer currentValue, Integer threshold, String description) {
        if (alerts.existsByDeviceIdAndTypeAndStatus(device.getId(), type, "OPEN")) return;
        MaintenanceAlert alert = alerts.save(MaintenanceAlert.builder().type(type).device(device).buildingId(device.getBuilding() == null ? null : device.getBuilding().getId()).currentValue(currentValue).threshold(threshold).description(description).build());
        notifications.enqueue(device.getOwner(), type, "PUSH", device.getOwner() == null ? "" : device.getOwner().getEmail(), false);
    }

    private MaintenanceAlert find(Long id) { return alerts.findById(id).orElseThrow(() -> new ResourceNotFoundException("Alerta de mantenimiento no encontrada")); }
    private <T> Specification<MaintenanceAlert> equal(String field, T value) { return value == null ? null : (root, query, cb) -> { jakarta.persistence.criteria.Path<?> path = root; for (String part : field.split("\\.")) path = path.get(part); return cb.equal(path, value); }; }
    private MaintenanceResponse response(MaintenanceAlert a) { return new MaintenanceResponse(a.getId(), a.getType(), a.getStatus(), a.getDevice().getId(), a.getDevice().getDeviceCode(), a.getBuildingId(), a.getCurrentValue(), a.getThreshold(), a.getDescription(), a.getAcknowledgedBy() == null ? null : a.getAcknowledgedBy().getId(), a.getAcknowledgedAt(), a.getResolvedBy() == null ? null : a.getResolvedBy().getId(), a.getResolvedAt(), a.getResolutionNote(), a.getCreatedAt()); }
}
