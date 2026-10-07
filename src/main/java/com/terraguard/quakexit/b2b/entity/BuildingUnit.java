package com.terraguard.quakexit.b2b.entity;

import com.terraguard.quakexit.common.entity.BaseEntity;
import com.terraguard.quakexit.iam.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "building_units", uniqueConstraints = @UniqueConstraint(
    name = "uk_building_unit_number", columnNames = {"building_id", "unit_number"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BuildingUnit extends BaseEntity {
    @Column(name = "unit_number", nullable = false, length = 30)
    private String unitNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resident_id", nullable = false)
    private User resident;
}
