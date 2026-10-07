package com.terraguard.quakexit.device.entity;

import com.terraguard.quakexit.b2b.entity.Building;
import com.terraguard.quakexit.b2b.entity.BuildingUnit;
import com.terraguard.quakexit.common.entity.BaseEntity;
import com.terraguard.quakexit.common.enums.DomainEnums.*;
import com.terraguard.quakexit.iam.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "devices", uniqueConstraints = {
    @UniqueConstraint(name = "uk_device_code", columnNames = "device_code"),
    @UniqueConstraint(name = "uk_device_mac", columnNames = "mac_address")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Device extends BaseEntity {
    @Column(name = "device_code", nullable = false, length = 80) private String deviceCode;
    @Column(name = "mac_address", nullable = false, length = 17) private String macAddress;
    @Column(length = 120) private String alias;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "owner_id") private User owner;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "building_id") private Building building;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "unit_id") private BuildingUnit unit;
    @Column(name = "firmware_version", length = 40) private String firmwareVersion;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) @Builder.Default private DeviceStatus status = DeviceStatus.OFFLINE;
    @Column(name = "last_seen_at") private Instant lastSeenAt;
    @Column(name = "battery_percentage") private Integer batteryPercentage;
    @Enumerated(EnumType.STRING) @Column(name = "power_mode", nullable = false, length = 20) @Builder.Default private PowerMode powerMode = PowerMode.NORMAL;
    private Double latitude;
    private Double longitude;
    @Enumerated(EnumType.STRING) @Column(name = "lock_status", nullable = false, length = 20) @Builder.Default private LockStatus lockStatus = LockStatus.UNKNOWN;
    @Enumerated(EnumType.STRING) @Column(name = "light_status", nullable = false, length = 20) @Builder.Default private LightStatus lightStatus = LightStatus.UNKNOWN;
    @Enumerated(EnumType.STRING) @Column(name = "siren_status", nullable = false, length = 20) @Builder.Default private AlarmStatus sirenStatus = AlarmStatus.INACTIVE;
    @Column(name = "component_updated_at") private Instant componentUpdatedAt;
}
