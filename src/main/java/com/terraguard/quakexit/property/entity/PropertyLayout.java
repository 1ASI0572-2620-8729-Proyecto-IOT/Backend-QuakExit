package com.terraguard.quakexit.property.entity;

import com.terraguard.quakexit.common.entity.BaseEntity;
import com.terraguard.quakexit.iam.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "property_layouts", uniqueConstraints = @UniqueConstraint(name = "uk_layout_owner", columnNames = "owner_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PropertyLayout extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "owner_id", nullable = false) private User owner;
    @Lob @Column(name = "structure_json", nullable = false) private String structureJson;
}
