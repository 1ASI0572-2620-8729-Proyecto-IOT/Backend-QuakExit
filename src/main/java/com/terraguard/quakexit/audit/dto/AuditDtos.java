package com.terraguard.quakexit.audit.dto;

import java.time.Instant;
import java.util.Map;

public final class AuditDtos {
    private AuditDtos() {}
    public record AuditResponse(Long id, Long userId, String userEmail, String role, String action,
                                String entityType, Long entityId, Long propertyId, Long buildingId,
                                Map<String, Object> detail, String ipAddress, Instant createdAt) {}
}
