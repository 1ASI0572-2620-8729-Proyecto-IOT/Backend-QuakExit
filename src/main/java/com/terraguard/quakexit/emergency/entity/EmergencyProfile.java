package com.terraguard.quakexit.emergency.entity;

import com.terraguard.quakexit.common.enums.BloodType;
import com.terraguard.quakexit.iam.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "emergency_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmergencyProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false, unique = true) private User user;
    @Enumerated(EnumType.STRING) @Column(name = "blood_type", length = 20) private BloodType bloodType;
    @Column(length = 1000) private String allergies;
    @Column(name = "medical_conditions", length = 1000) private String medicalConditions;
    @Column(length = 1000) private String medications;
    @Column(length = 1000) private String notes;
}
