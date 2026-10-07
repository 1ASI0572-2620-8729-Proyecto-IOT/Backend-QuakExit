package com.terraguard.quakexit.b2b.service;

import com.terraguard.quakexit.b2b.dto.B2bDtos.*;
import com.terraguard.quakexit.b2b.entity.*;
import com.terraguard.quakexit.b2b.repository.*;
import com.terraguard.quakexit.common.enums.DomainEnums.DeviceStatus;
import com.terraguard.quakexit.common.exception.ApiExceptions.*;
import com.terraguard.quakexit.device.entity.Device;
import com.terraguard.quakexit.device.repository.DeviceRepository;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.iam.repository.UserRepository;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class BuildingUnitService {
    private final BuildingUnitRepository units;
    private final BuildingRepository buildings;
    private final UserRepository users;
    private final DeviceRepository devices;

    @Transactional(readOnly = true)
    public List<UnitResponse> list(User manager) {
        return units.findByBuildingOwnerIdOrderByUnitNumber(manager.getId()).stream()
            .map(this::response).toList();
    }

    @Transactional
    public UnitResponse create(CreateUnitRequest request, User manager) {
        Building building = buildings.findByIdAndOwnerId(request.buildingId(), manager.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Edificio no encontrado"));
        if (units.existsByBuildingIdAndUnitNumber(building.getId(), request.unit())) {
            throw new DuplicateResourceException("La unidad ya existe en el edificio");
        }
        User resident = users.findById(request.residentId())
            .orElseThrow(() -> new ResourceNotFoundException("Residente no encontrado"));
        BuildingUnit unit = units.save(BuildingUnit.builder()
            .unitNumber(request.unit()).building(building).resident(resident).build());
        for (Long deviceId : Optional.ofNullable(request.deviceIds()).orElseGet(List::of)) {
            Device device = devices.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo no encontrado: " + deviceId));
            if (device.getOwner() == null || !device.getOwner().getId().equals(resident.getId())) {
                throw new BusinessRuleException("El dispositivo no pertenece al residente");
            }
            device.setBuilding(building);
            device.setUnit(unit);
            devices.save(device);
        }
        return response(unit);
    }

    private UnitResponse response(BuildingUnit unit) {
        List<Device> assigned = devices.findByUnitId(unit.getId());
        String status = assigned.stream().anyMatch(d -> d.getStatus() == DeviceStatus.ALERT)
            ? "ALERT"
            : assigned.isEmpty() || assigned.stream().allMatch(d -> d.getStatus() == DeviceStatus.OFFLINE)
                ? "OFFLINE" : "SAFE";
        return new UnitResponse("unit-" + unit.getUnitNumber(), unit.getUnitNumber(),
            unit.getResident().getFullName(), status, assigned.size());
    }
}
