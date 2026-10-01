package com.terraguard.quakexit.simulation.dto;

import com.terraguard.quakexit.common.enums.DomainEnums.SimulationType;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimulationDtos {
    private SimulationDtos() {}
    public enum SimulationTarget { COMMON_AREAS, PRIVATE_HOME }
    public record TriggerRequest(@NotNull SimulationTarget target, SimulationType type) {}
    public record TriggerResponse(@JsonProperty("simulation_active") boolean simulationActive, @JsonProperty("duration_seconds") int durationSeconds, String target) {}
}
