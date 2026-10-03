package com.terraguard.quakexit.property.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.terraguard.quakexit.common.exception.ApiExceptions.BusinessRuleException;
import com.terraguard.quakexit.common.exception.ApiExceptions.ResourceNotFoundException;
import com.terraguard.quakexit.device.entity.Device;
import com.terraguard.quakexit.device.repository.DeviceRepository;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.property.dto.PropertyDtos.*;
import com.terraguard.quakexit.property.entity.PropertyLayout;
import java.util.HashSet;
import java.util.Set;
import com.terraguard.quakexit.property.repository.PropertyLayoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class PropertyService {
    private final PropertyLayoutRepository layouts;
    private final DeviceRepository devices;
    private final ObjectMapper mapper;

    @Transactional
    public LayoutResponse setup(SetupRequest request, User owner) {
        PropertyLayout layout = layouts.findByOwnerId(owner.getId()).orElseGet(PropertyLayout::new);
        layout.setOwner(owner);
        validateLevels(request, owner);
        try { layout.setStructureJson(mapper.writeValueAsString(normalize(mapper.valueToTree(request.levels())))); }
        catch (JsonProcessingException ex) { throw new IllegalStateException("No se pudo guardar la estructura de la vivienda", ex); }
        return response(layouts.save(layout));
    }

    @Transactional(readOnly = true)
    public LayoutResponse layout(User owner) {
        return layouts.findByOwnerId(owner.getId()).map(this::response)
            .orElseThrow(() -> new ResourceNotFoundException("La vivienda aun no tiene una estructura configurada"));
    }

    private LayoutResponse response(PropertyLayout layout) {
        try { JsonNode levels = normalize(mapper.readTree(layout.getStructureJson())); return new LayoutResponse(layout.getId(), layout.getOwner().getId(), levels); }
        catch (JsonProcessingException ex) { throw new IllegalStateException("La estructura de la vivienda no es valida", ex); }
    }

    private ArrayNode normalize(JsonNode source) {
        ArrayNode levels = (ArrayNode) source;
        for (int levelIndex = 0; levelIndex < levels.size(); levelIndex++) {
            ObjectNode level = (ObjectNode) levels.get(levelIndex);
            int floor = level.path("floor").asInt();
            if (!level.hasNonNull("id")) level.put("id", floor);
            JsonNode roomsNode = level.get("rooms");
            if (!(roomsNode instanceof ArrayNode rooms)) continue;
            for (int roomIndex = 0; roomIndex < rooms.size(); roomIndex++) {
                ObjectNode room = (ObjectNode) rooms.get(roomIndex);
                if (!room.hasNonNull("id")) room.put("id", ((long) floor * 1_000_000L) + roomIndex + 1);
                if (!room.hasNonNull("type")) room.put("type", RoomType.SPACE.name());
                if (!room.has("deviceIds") || room.get("deviceIds").isNull()) room.set("deviceIds", mapper.createArrayNode());
            }
        }
        return levels;
    }

    private void validateLevels(SetupRequest request, User owner) {
        Set<Integer> floors = new HashSet<>();
        Set<Long> deviceIds = new HashSet<>();
        for (LevelRequest level : request.levels()) {
            if (!floors.add(level.floor())) throw new BusinessRuleException("No pueden existir pisos duplicados");
            for (RoomRequest room : level.rooms()) {
                Set<Long> roomDeviceIds = new HashSet<>(room.deviceIds());
                if (roomDeviceIds.size() != room.deviceIds().size() || !deviceIds.addAll(roomDeviceIds)) {
                    throw new BusinessRuleException("Un dispositivo no puede repetirse en la vivienda");
                }
            }
        }
        if (deviceIds.isEmpty()) return;
        java.util.List<Device> ownedDevices = devices.findAllById(deviceIds);
        if (ownedDevices.size() != deviceIds.size() || ownedDevices.stream().anyMatch(device -> device.getOwner() == null || !device.getOwner().getId().equals(owner.getId()))) {
            throw new BusinessRuleException("Todos los dispositivos deben pertenecer al usuario autenticado");
        }
    }
}
