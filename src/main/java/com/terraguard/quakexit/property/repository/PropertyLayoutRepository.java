package com.terraguard.quakexit.property.repository;

import com.terraguard.quakexit.property.entity.PropertyLayout;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyLayoutRepository extends JpaRepository<PropertyLayout, Long> {
    Optional<PropertyLayout> findByOwnerId(Long ownerId);
}
