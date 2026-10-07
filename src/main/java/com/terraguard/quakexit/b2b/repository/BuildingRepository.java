package com.terraguard.quakexit.b2b.repository;

import com.terraguard.quakexit.b2b.entity.Building;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    Optional<Building> findByIdAndOwnerId(Long id, Long ownerId);
    List<Building> findByOwnerIdOrderByName(Long ownerId);
}
