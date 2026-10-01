package com.terraguard.quakexit.maintenance.dto;

import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class MaintenanceDtos {
    private MaintenanceDtos() {}
    public record ResolutionRequest(@Size(max = 500) String resolutionNote) {}
    public record MaintenanceResponse(Long id, String type, String status, Long deviceId, String deviceCode, Long buildingId, Integer currentValue, Integer threshold, String description, Long acknowledgedBy, Instant acknowledgedAt, Long resolvedBy, Instant resolvedAt, String resolutionNote, Instant createdAt) {}
}
