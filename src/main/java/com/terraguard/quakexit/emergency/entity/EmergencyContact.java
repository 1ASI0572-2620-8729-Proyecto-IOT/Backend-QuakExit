package com.terraguard.quakexit.emergency.entity;

import com.terraguard.quakexit.iam.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "emergency_contacts", uniqueConstraints = @UniqueConstraint(name = "uk_contact_user_phone", columnNames = {"user_id", "phone_number"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmergencyContact {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(name = "full_name", nullable = false, length = 120) private String fullName;
    @Column(name = "phone_number", nullable = false, length = 20) private String phoneNumber;
    @Column(length = 80) private String relationship;
    @Column(name = "notify_by_sms", nullable = false) @Builder.Default private boolean notifyBySms = true;
    @Column(nullable = false) @Builder.Default private int priority = 1;
}
