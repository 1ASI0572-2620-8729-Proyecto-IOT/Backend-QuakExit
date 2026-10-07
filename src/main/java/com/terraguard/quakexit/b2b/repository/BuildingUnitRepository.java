package com.terraguard.quakexit.b2b.repository;

import com.terraguard.quakexit.b2b.entity.BuildingUnit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingUnitRepository extends JpaRepository<BuildingUnit, Long> {
    List<BuildingUnit> findByBuildingOwnerIdOrderByUnitNumber(Long ownerId);
    Optional<BuildingUnit> findByIdAndBuildingOwnerId(Long id, Long ownerId);
    boolean existsByBuildingIdAndUnitNumber(Long buildingId, String unitNumber);
}
