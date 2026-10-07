package com.terraguard.quakexit.b2b.service;

import com.terraguard.quakexit.b2b.dto.B2bDtos.*;
import com.terraguard.quakexit.b2b.entity.Building;
import com.terraguard.quakexit.b2b.repository.BuildingRepository;
import com.terraguard.quakexit.iam.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class BuildingService {
    private final BuildingRepository buildings;

    @Transactional
    public BuildingResponse create(CreateBuildingRequest request, User owner) {
        Building building = Building.builder()
            .name(request.name())
            .address(request.address())
            .district(request.district())
            .city(request.city())
            .latitude(request.latitude())
            .longitude(request.longitude())
            .totalFloors(request.totalFloors())
            .owner(owner)
            .build();
        return response(buildings.save(building));
    }

    @Transactional(readOnly = true)
    public List<BuildingResponse> list(User owner) {
        return buildings.findByOwnerIdOrderByName(owner.getId()).stream().map(this::response).toList();
    }

    private BuildingResponse response(Building building) {
        return new BuildingResponse(building.getId(), building.getName(), building.getAddress(),
            building.getDistrict(), building.getCity(), building.getLatitude(), building.getLongitude(),
            building.getTotalFloors());
    }
}
