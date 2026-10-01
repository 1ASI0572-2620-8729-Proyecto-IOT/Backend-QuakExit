package com.terraguard.quakexit.b2b.entity;

import com.terraguard.quakexit.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "safe_zones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SafeZone extends BaseEntity {
    @Column(nullable = false, length = 120) private String name;
    @Column(length = 500) private String description;
    @Column(name = "max_capacity", nullable = false) private Integer maxCapacity;
    @Column(name = "current_occupancy", nullable = false) @Builder.Default private Integer currentOccupancy = 0;
    private Double latitude;
    private Double longitude;
    @Column(name = "floor_level") private Integer floorLevel;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "building_id", nullable = false) private Building building;
}
