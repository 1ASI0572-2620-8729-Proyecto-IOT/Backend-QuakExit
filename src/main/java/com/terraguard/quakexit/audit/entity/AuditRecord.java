package com.terraguard.quakexit.audit.entity;

import com.terraguard.quakexit.common.entity.BaseEntity;
import com.terraguard.quakexit.iam.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "audit_records", indexes = {
    @Index(name = "idx_audit_created_at", columnList = "created_at"),
    @Index(name = "idx_audit_user", columnList = "user_id"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_building", columnList = "building_id"),
    @Index(name = "idx_audit_property", columnList = "property_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditRecord extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;
    @Column(name = "role", length = 30) private String role;
    @Column(nullable = false, length = 80) private String action;
    @Column(name = "entity_type", length = 80) private String entityType;
    @Column(name = "entity_id") private Long entityId;
    @Column(name = "property_id") private Long propertyId;
    @Column(name = "building_id") private Long buildingId;
    @Lob @Column(name = "detail_json") private String detailJson;
    @Column(name = "ip_address", length = 64) private String ipAddress;
}
