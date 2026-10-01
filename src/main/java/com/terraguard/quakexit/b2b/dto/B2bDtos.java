package com.terraguard.quakexit.b2b.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public final class B2bDtos {
    private B2bDtos() {}
    public record BulkRegisterRequest(@NotNull Long buildingId, @NotEmpty List<@Valid DeviceRegistration> devices) {}
    public record DeviceRegistration(@NotBlank String deviceCode, @NotBlank @Pattern(regexp="^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$") String macAddress, @NotBlank String alias) {}
    public record BulkRegisterResponse(int registered, int skipped) {}
    public record AlarmResponse(boolean activated, int devicesAffected, String scope) {}
}
