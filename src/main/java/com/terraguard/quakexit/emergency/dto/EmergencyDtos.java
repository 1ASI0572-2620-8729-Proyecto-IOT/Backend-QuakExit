package com.terraguard.quakexit.emergency.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;
import com.terraguard.quakexit.common.enums.DomainEnums.*;

public final class EmergencyDtos {
    private EmergencyDtos() {}
    public record ReadingRequest(@NotBlank String deviceCode, @NotNull Instant timestamp, double ax, double ay, double az, Double freqHz, @Min(0) @Max(100) Integer battery) {}
    public record EventResponse(Long id, Long deviceId, EventStatus status, Severity severity, double peakAcceleration, Instant detectedAt, Instant resolvedAt, int smsNotificationsSent) {}
    public record FalseAlarmRequest(@NotBlank @Size(max=500) String reason) {}
}
