package com.terraguard.quakexit.property.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public final class PropertyDtos {
    private PropertyDtos() {}
    public record SetupRequest(@NotEmpty List<@Valid LevelRequest> levels) {}
    public record LevelRequest(@Min(1) int floor, @NotBlank @Size(max=80) String name, @NotEmpty List<@Valid RoomRequest> rooms) {}
    public record RoomRequest(@NotBlank @Size(max=120) String name) {}
    public record LayoutResponse(Long id, Long ownerId, JsonNode levels) {}
}
