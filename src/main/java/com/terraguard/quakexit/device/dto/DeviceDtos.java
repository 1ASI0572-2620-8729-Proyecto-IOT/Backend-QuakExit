package com.terraguard.quakexit.device.dto;

import jakarta.validation.constraints.*;
import com.terraguard.quakexit.common.enums.DomainEnums.*;
import java.time.Instant;

public final class DeviceDtos {
    private DeviceDtos() {}
    public record BindRequest(@NotBlank String deviceCode, @NotBlank @Pattern(regexp="^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$") String macAddress, @NotBlank @Size(max=120) String alias) {}
    public record DeviceResponse(Long id, String deviceCode, String macAddress, String alias, DeviceStatus status, LockStatus lockStatus, LightStatus lightStatus, AlarmStatus sirenStatus, Instant lastSeenAt, Integer batteryPercentage, PowerMode powerMode) {}
    public record PowerModeRequest(@NotNull PowerMode powerMode) {}
    public record BatteryStatusResponse(Long deviceId, String deviceCode, String alias, Integer batteryPercentage, DeviceStatus status, Instant lastSeenAt) {}
}
