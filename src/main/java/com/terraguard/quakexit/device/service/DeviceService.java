package com.terraguard.quakexit.device.service;

import com.terraguard.quakexit.common.enums.DomainEnums.*;
import com.terraguard.quakexit.common.exception.ApiExceptions.*;
import com.terraguard.quakexit.audit.service.AuditService;
import com.terraguard.quakexit.device.dto.DeviceDtos.*;
import com.terraguard.quakexit.device.entity.Device;
import com.terraguard.quakexit.device.repository.DeviceRepository;
import com.terraguard.quakexit.iam.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;

@Service @RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository devices; private final AuditService audit;
    @Transactional public DeviceResponse bind(BindRequest request, User user) {
        if (devices.findByDeviceCode(request.deviceCode()).map(d -> d.getOwner() != null && !d.getOwner().getId().equals(user.getId())).orElse(false)) throw new DuplicateResourceException("El dispositivo ya pertenece a otro usuario");
        Device device = devices.findByDeviceCode(request.deviceCode()).orElseGet(Device::new); device.setDeviceCode(request.deviceCode()); device.setMacAddress(request.macAddress().toUpperCase()); device.setAlias(request.alias()); device.setOwner(user); Device saved = devices.save(device); audit.record(user, "DEVICE_BOUND", "DEVICE", saved.getId(), saved.getOwner().getId(), saved.getBuilding() == null ? null : saved.getBuilding().getId(), null, null); return toResponse(saved);
    }
    @Transactional(readOnly=true) public Device owned(Long id, User user) { Device d = devices.findById(id).orElseThrow(() -> new ResourceNotFoundException("Dispositivo no encontrado")); if (d.getOwner() == null || !d.getOwner().getId().equals(user.getId())) throw new org.springframework.security.access.AccessDeniedException("Dispositivo ajeno"); return d; }
    @Transactional(readOnly=true) public DeviceResponse status(Long id, User user) { Device d = owned(id,user); if (d.getLastSeenAt() == null || d.getLastSeenAt().isBefore(Instant.now().minus(5, java.time.temporal.ChronoUnit.MINUTES))) d.setStatus(DeviceStatus.OFFLINE); return toResponse(d); }
    @Transactional(readOnly=true) public java.util.List<DeviceResponse> list(User user) { return devices.findByOwnerId(user.getId()).stream().map(this::toResponse).toList(); }
    @Transactional public DeviceResponse power(Long id, PowerModeRequest request, User user) { Device d=owned(id,user); PowerMode previous = d.getPowerMode(); d.setPowerMode(request.powerMode()); Device saved = devices.save(d); audit.record(user, "DEVICE_POWER_MODE_CHANGED", "DEVICE", saved.getId(), user.getId(), saved.getBuilding() == null ? null : saved.getBuilding().getId(), java.util.Map.of("previousMode", previous.name(), "newMode", request.powerMode().name()), null); return toResponse(saved); }
    @Transactional public DeviceResponse unlockPrivate(Long id, User user) {
        if (user.getRole() == Role.BUILDING_ADMIN) throw new org.springframework.security.access.AccessDeniedException("BUILDING_ADMIN no puede abrir puertas privadas");
        Device d = owned(id, user); d.setLockStatus(LockStatus.UNLOCKED); Device saved = devices.save(d); audit.record(user, "DEVICE_UNLOCKED", "DEVICE", saved.getId(), user.getId(), saved.getBuilding() == null ? null : saved.getBuilding().getId(), null, null); return toResponse(saved);
    }
    @Transactional(readOnly=true) public java.util.List<BatteryStatusResponse> batteryStatus(User user) { return devices.findByOwnerId(user.getId()).stream().map(d -> new BatteryStatusResponse(d.getId(), d.getDeviceCode(), d.getAlias(), d.getBatteryPercentage(), d.getStatus(), d.getLastSeenAt())).toList(); }
    public DeviceResponse toResponse(Device d) { return new DeviceResponse(d.getId(),d.getDeviceCode(),d.getMacAddress(),d.getAlias(),d.getStatus(),d.getLockStatus(),d.getLightStatus(),d.getSirenStatus(),d.getLastSeenAt(),d.getBatteryPercentage(),d.getPowerMode()); }
}
