package com.terraguard.quakexit.maintenance.entity;

import com.terraguard.quakexit.common.entity.BaseEntity;
import com.terraguard.quakexit.device.entity.Device;
import com.terraguard.quakexit.iam.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "maintenance_alerts", indexes = {
    @Index(name = "idx_maintenance_status", columnList = "status"),
    @Index(name = "idx_maintenance_device", columnList = "device_id"),
    @Index(name = "idx_maintenance_building", columnList = "building_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaintenanceAlert extends BaseEntity {
    @Column(nullable = false, length = 40) private String type;
    @Column(nullable = false, length = 20) @Builder.Default private String status = "OPEN";
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "device_id", nullable = false) private Device device;
    @Column(name = "building_id") private Long buildingId;
    private Integer currentValue;
    private Integer threshold;
    @Column(length = 500) private String description;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "acknowledged_by") private User acknowledgedBy;
    @Column(name = "acknowledged_at") private java.time.Instant acknowledgedAt;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "resolved_by") private User resolvedBy;
    @Column(name = "resolved_at") private java.time.Instant resolvedAt;
    @Column(name = "resolution_note", length = 500) private String resolutionNote;
}
