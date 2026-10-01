package com.terraguard.quakexit.simulation.service;

import com.terraguard.quakexit.common.enums.DomainEnums.*;
import com.terraguard.quakexit.common.exception.ApiExceptions.BusinessRuleException;
import com.terraguard.quakexit.audit.service.AuditService;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.simulation.dto.SimulationDtos.*;
import com.terraguard.quakexit.simulation.entity.Simulation;
import com.terraguard.quakexit.simulation.repository.SimulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class SimulationService {
    private final SimulationRepository simulations; private final AuditService audit;
    @Transactional public TriggerResponse trigger(TriggerRequest request, User user) {
        if (user.getRole() == Role.BUILDING_ADMIN && request.target() == SimulationTarget.PRIVATE_HOME) throw new BusinessRuleException("BUILDING_ADMIN solo puede activar alarmas de areas comunes");
        Simulation simulation = Simulation.builder().type(request.type() == null ? SimulationType.MANUAL : request.type()).status(SimulationStatus.RUNNING).startedAt(java.time.Instant.now()).triggeredBy(user).build();
        simulations.save(simulation);
        audit.record(user, "SIMULATION_STARTED", "SIMULATION", simulation.getId(), null, simulation.getBuilding() == null ? null : simulation.getBuilding().getId(), java.util.Map.of("target", request.target().name(), "type", simulation.getType().name()), null);
        return new TriggerResponse(true, 30, request.target().name());
    }
}
