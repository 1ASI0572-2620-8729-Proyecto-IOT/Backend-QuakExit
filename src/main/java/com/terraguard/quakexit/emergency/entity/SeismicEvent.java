package com.terraguard.quakexit.emergency.entity;

import com.terraguard.quakexit.b2b.entity.Building;
import com.terraguard.quakexit.common.enums.DomainEnums.*;
import com.terraguard.quakexit.device.entity.Device;
import com.terraguard.quakexit.iam.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "seismic_events", indexes = @Index(name = "idx_event_status_detected", columnList = "status,detected_at"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SeismicEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "device_id", nullable = false) private Device device;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "building_id") private Building building;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private EventStatus status;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Severity severity;
    @Column(name = "peak_acceleration", nullable = false) private double peakAcceleration;
    @Column(name = "detected_at", nullable = false) private Instant detectedAt;
    @Column(name = "resolved_at") private Instant resolvedAt;
    @Column(name = "false_alarm_reason", length = 500) private String falseAlarmReason;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cancelled_by") private User cancelledBy;
    @Column(name = "sms_notifications_sent", nullable = false) @Builder.Default private int smsNotificationsSent = 0;
    @Column(name = "doors_unlocked_at") private Instant doorsUnlockedAt;
}
