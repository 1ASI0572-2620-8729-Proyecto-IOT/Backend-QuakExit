package com.terraguard.quakexit.property.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terraguard.quakexit.common.exception.ApiExceptions.ResourceNotFoundException;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.property.dto.PropertyDtos.*;
import com.terraguard.quakexit.property.entity.PropertyLayout;
import com.terraguard.quakexit.property.repository.PropertyLayoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class PropertyService {
    private final PropertyLayoutRepository layouts;
    private final ObjectMapper mapper;

    @Transactional
    public LayoutResponse setup(SetupRequest request, User owner) {
        PropertyLayout layout = layouts.findByOwnerId(owner.getId()).orElseGet(PropertyLayout::new);
        layout.setOwner(owner);
        try { layout.setStructureJson(mapper.writeValueAsString(request.levels())); }
        catch (JsonProcessingException ex) { throw new IllegalStateException("No se pudo guardar la estructura de la vivienda", ex); }
        return response(layouts.save(layout));
    }

    @Transactional(readOnly = true)
    public LayoutResponse layout(User owner) {
        return layouts.findByOwnerId(owner.getId()).map(this::response)
            .orElseThrow(() -> new ResourceNotFoundException("La vivienda aun no tiene una estructura configurada"));
    }

    private LayoutResponse response(PropertyLayout layout) {
        try { JsonNode levels = mapper.readTree(layout.getStructureJson()); return new LayoutResponse(layout.getId(), layout.getOwner().getId(), levels); }
        catch (JsonProcessingException ex) { throw new IllegalStateException("La estructura de la vivienda no es valida", ex); }
    }
}
