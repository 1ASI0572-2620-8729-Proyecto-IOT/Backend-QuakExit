package com.terraguard.quakexit.b2b.entity;

import com.terraguard.quakexit.common.entity.BaseEntity;
import com.terraguard.quakexit.iam.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "buildings", indexes = @Index(name = "idx_building_owner", columnList = "owner_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Building extends BaseEntity {
    @Column(nullable = false, length = 160) private String name;
    @Column(nullable = false, length = 240) private String address;
    @Column(length = 100) private String district;
    @Column(length = 100) private String city;
    private Double latitude;
    private Double longitude;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "owner_id", nullable = false) private User owner;
    @Column(name = "total_floors") private Integer totalFloors;
}
