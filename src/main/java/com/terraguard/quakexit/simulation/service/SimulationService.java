package com.terraguard.quakexit.simulation.service;

import com.terraguard.quakexit.common.enums.DomainEnums.*;
import com.terraguard.quakexit.common.exception.ApiExceptions.BusinessRuleException;
import com.terraguard.quakexit.audit.service.AuditService;
import com.terraguard.quakexit.device.entity.Device;
import com.terraguard.quakexit.device.repository.DeviceRepository;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.simulation.dto.SimulationDtos.*;
import com.terraguard.quakexit.simulation.entity.Simulation;
import com.terraguard.quakexit.simulation.entity.SimulationResult;
import com.terraguard.quakexit.simulation.repository.SimulationRepository;
import com.terraguard.quakexit.simulation.repository.SimulationResultRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class SimulationService {
    private static final int DURATION_SECONDS = 30;
    private final SimulationRepository simulations;
    private final SimulationResultRepository simulationResults;
    private final DeviceRepository devices;
    private final AuditService audit;
    @Transactional public TriggerResponse trigger(TriggerRequest request, User user) {
        validatePermission(request.target(), user);
        List<Device> targetDevices = targetDevices(request.target(), user);
        Simulation simulation = Simulation.builder().type(request.type() == null ? SimulationType.MANUAL : request.type()).status(SimulationStatus.RUNNING).startedAt(java.time.Instant.now()).triggeredBy(user).build();
        simulations.save(simulation);
        List<DeviceActionResponse> actionResults = new ArrayList<>();
        List<SimulationResult> persistedResults = new ArrayList<>();
        for (Device device : targetDevices) {
            try {
                device.setLightStatus(LightStatus.ON);
                device.setSirenStatus(AlarmStatus.ACTIVE);
                device.setStatus(DeviceStatus.ALERT);
                devices.save(device);
                actionResults.add(new DeviceActionResponse(device.getId(), true, null));
                persistedResults.add(SimulationResult.builder().simulation(simulation).device(device).success(true).build());
            } catch (RuntimeException ex) {
                actionResults.add(new DeviceActionResponse(device.getId(), false, ex.getMessage()));
                persistedResults.add(SimulationResult.builder().simulation(simulation).device(device).success(false).errorMessage(ex.getMessage()).build());
            }
        }
        simulationResults.saveAll(persistedResults);
        long failedActions = actionResults.stream().filter(result -> !result.success()).count();
        audit.record(user, "SIMULATION_STARTED", "SIMULATION", simulation.getId(), null, null, java.util.Map.of("target", request.target().name(), "type", simulation.getType().name(), "devicesAffected", targetDevices.size(), "failedActions", failedActions), null);
        return new TriggerResponse("sim-" + simulation.getId(), true, DURATION_SECONDS, request.target().name(), actionResults);
    }

    private void validatePermission(SimulationTarget target, User user) {
        if (target == SimulationTarget.PRIVATE_HOME && (user.getRole() == Role.BUILDING_ADMIN || user.getRole() == Role.B2B_ADMIN)) {
            throw new BusinessRuleException("Este usuario solo puede activar simulacros de areas comunes");
        }
        if (target == SimulationTarget.COMMON_AREAS && user.getRole() == Role.HOMEOWNER) {
            throw new BusinessRuleException("El usuario no puede activar simulacros de areas comunes");
        }
    }

    private List<Device> targetDevices(SimulationTarget target, User user) {
        return target == SimulationTarget.PRIVATE_HOME ? devices.findByOwnerId(user.getId()) : devices.findByBuildingOwnerId(user.getId());
    }
}
