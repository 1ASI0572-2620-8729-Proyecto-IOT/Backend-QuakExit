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
    public record CreateUnitRequest(@NotNull Long buildingId, @NotBlank @Size(max = 30) String unit,
                                    @NotNull Long residentId, List<@Positive Long> deviceIds) {}
    public record UnitResponse(String id, String unit, String resident, String status, int devices) {}
    public record CreateBuildingRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 240) String address,
        @Size(max = 100) String district,
        @Size(max = 100) String city,
        Double latitude,
        Double longitude,
        @Positive Integer totalFloors
    ) {}
    public record BuildingResponse(Long id, String name, String address, String district, String city,
                                   Double latitude, Double longitude, Integer totalFloors) {}
}
