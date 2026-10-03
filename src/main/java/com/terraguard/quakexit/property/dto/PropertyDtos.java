package com.terraguard.quakexit.property.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public final class PropertyDtos {
    private PropertyDtos() {}
    public enum RoomType { DOOR, SPACE }
    public record SetupRequest(@NotEmpty List<@Valid LevelRequest> levels) {}
    public record LevelRequest(Long id, @Min(1) int floor, @NotBlank @Size(max=80) String name, @NotNull List<@Valid RoomRequest> rooms) {}
    public record RoomRequest(Long id, @NotBlank @Size(max=120) String name, RoomType type, List<@Positive Long> deviceIds) {
        public RoomRequest {
            type = type == null ? RoomType.SPACE : type;
            deviceIds = deviceIds == null ? List.of() : List.copyOf(deviceIds);
        }
    }
    public record LayoutResponse(Long id, Long ownerId, JsonNode levels) {}
}
