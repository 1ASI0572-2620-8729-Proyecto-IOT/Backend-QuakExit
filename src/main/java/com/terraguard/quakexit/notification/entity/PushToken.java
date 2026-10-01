package com.terraguard.quakexit.notification.entity;

import com.terraguard.quakexit.common.entity.BaseEntity;
import com.terraguard.quakexit.iam.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "push_tokens", uniqueConstraints = @UniqueConstraint(name = "uk_push_token", columnNames = "token"), indexes = @Index(name = "idx_push_token_user", columnList = "user_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PushToken extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(nullable = false, length = 500) private String token;
    @Column(nullable = false, length = 20) private String platform;
    @Column(name = "device_name", length = 120) private String deviceName;
    @Builder.Default @Column(nullable = false) private boolean active = true;
}
