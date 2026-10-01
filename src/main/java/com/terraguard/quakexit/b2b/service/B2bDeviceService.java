package com.terraguard.quakexit.b2b.service;

import com.terraguard.quakexit.b2b.dto.B2bDtos.*;
import com.terraguard.quakexit.b2b.entity.Building;
import com.terraguard.quakexit.b2b.repository.BuildingRepository;
import com.terraguard.quakexit.common.enums.DomainEnums.*;
import com.terraguard.quakexit.common.exception.ApiExceptions.ResourceNotFoundException;
import com.terraguard.quakexit.audit.service.AuditService;
import com.terraguard.quakexit.device.entity.Device;
import com.terraguard.quakexit.device.repository.DeviceRepository;
import com.terraguard.quakexit.iam.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class B2bDeviceService {
    private final DeviceRepository devices;
    private final BuildingRepository buildings;
    private final AuditService audit;
    @Transactional public BulkRegisterResponse bulkRegister(BulkRegisterRequest request, User user) {
        Building building = buildings.findByIdAndOwnerId(request.buildingId(), user.getId()).orElseThrow(() -> new ResourceNotFoundException("Edificio no encontrado"));
        int registered = 0, skipped = 0;
        for (DeviceRegistration item : request.devices()) {
            if (devices.findByDeviceCode(item.deviceCode()).isPresent() || devices.findByMacAddress(item.macAddress().toUpperCase()).isPresent()) { skipped++; continue; }
            devices.save(Device.builder().deviceCode(item.deviceCode()).macAddress(item.macAddress().toUpperCase()).alias(item.alias()).building(building).status(DeviceStatus.OFFLINE).build());
            registered++;
        }
        audit.record(user, "BULK_DEVICES_REGISTERED", "BUILDING", building.getId(), null, building.getId(), java.util.Map.of("registered", registered, "skipped", skipped, "source", "JSON"), null);
        return new BulkRegisterResponse(registered, skipped);
    }
    @Transactional public BulkRegisterResponse bulkRegisterCsv(Long buildingId, org.springframework.web.multipart.MultipartFile file, User user) {
        Building building = buildings.findByIdAndOwnerId(buildingId, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Edificio no encontrado"));
        int registered = 0, skipped = 0;
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(file.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
            String line; boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                if (header && line.toLowerCase(java.util.Locale.ROOT).contains("devicecode")) { header = false; continue; }
                header = false;
                String[] values = line.split(",", -1);
                if (values.length < 3 || devices.findByDeviceCode(values[0].trim()).isPresent() || devices.findByMacAddress(values[1].trim().toUpperCase()).isPresent()) { skipped++; continue; }
                devices.save(Device.builder().deviceCode(values[0].trim()).macAddress(values[1].trim().toUpperCase()).alias(values[2].trim()).building(building).status(DeviceStatus.OFFLINE).build());
                registered++;
            }
        } catch (java.io.IOException ex) { throw new IllegalArgumentException("No se pudo leer el archivo CSV", ex); }
        audit.record(user, "BULK_DEVICES_REGISTERED", "BUILDING", building.getId(), null, building.getId(), java.util.Map.of("registered", registered, "skipped", skipped, "source", "CSV"), null);
        return new BulkRegisterResponse(registered, skipped);
    }
    @Transactional public AlarmResponse triggerAll(Long buildingId, User user) {
        buildings.findByIdAndOwnerId(buildingId, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Edificio no encontrado"));
        var buildingDevices = devices.findByBuildingId(buildingId);
        buildingDevices.forEach(device -> { device.setLightStatus(LightStatus.ON); device.setSirenStatus(AlarmStatus.ACTIVE); device.setStatus(DeviceStatus.ALERT); });
        devices.saveAll(buildingDevices);
        audit.record(user, "MASS_ALARM_TRIGGERED", "BUILDING", buildingId, null, buildingId, java.util.Map.of("devicesAffected", buildingDevices.size()), null);
        return new AlarmResponse(true, buildingDevices.size(), "COMMON_AREAS");
    }
}
