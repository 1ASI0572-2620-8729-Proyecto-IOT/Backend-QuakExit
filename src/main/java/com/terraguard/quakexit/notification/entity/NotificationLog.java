package com.terraguard.quakexit.notification.entity;

import com.terraguard.quakexit.common.entity.BaseEntity;
import com.terraguard.quakexit.iam.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_logs", indexes = {
    @Index(name = "idx_notification_user_created", columnList = "user_id,created_at"),
    @Index(name = "idx_notification_status", columnList = "status"),
    @Index(name = "idx_notification_type", columnList = "notification_type")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationLog extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;
    @Column(name = "notification_type", nullable = false, length = 60) private String notificationType;
    @Column(nullable = false, length = 20) private String channel;
    @Column(nullable = false, length = 30) private String recipient;
    @Column(nullable = false, length = 20) private String status;
    @Column(nullable = false) @Builder.Default private int attempts = 0;
    @Column(name = "simulation", nullable = false) @Builder.Default private boolean simulation = false;
    @Column(name = "error_message", length = 500) private String errorMessage;
}
