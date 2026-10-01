package com.terraguard.quakexit.notification.entity;

import com.terraguard.quakexit.iam.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_preferences")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationPreference {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false, unique = true) private User user;
    @Builder.Default private boolean pushEnabled = true;
    @Builder.Default private boolean smsEnabled = true;
    @Builder.Default private boolean whatsappEnabled = false;
    @Builder.Default private boolean earthquakeAlerts = true;
    @Builder.Default private boolean emergencyAlerts = true;
    @Builder.Default private boolean massAlarmAlerts = true;
    @Builder.Default private boolean lowBatteryAlerts = false;
    @Builder.Default private boolean offlineDeviceAlerts = true;
}
