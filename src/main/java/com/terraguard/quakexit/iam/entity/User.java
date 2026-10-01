package com.terraguard.quakexit.iam.entity;

import com.terraguard.quakexit.common.entity.BaseEntity;
import com.terraguard.quakexit.common.enums.DomainEnums.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User extends BaseEntity {
    @Column(nullable = false, length = 120) private String fullName;
    @Column(nullable = false, length = 180) private String email;
    @Column(name = "phone_number", length = 20) private String phoneNumber;
    @Column(name = "password_hash", nullable = false, length = 255) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Role role;
    @Enumerated(EnumType.STRING) @Column(name = "property_type", length = 20) private PropertyType propertyType;
    @Enumerated(EnumType.STRING) @Column(name = "ownership_role", length = 20) private OwnershipRole ownershipRole;
    @Column(nullable = false) @Builder.Default private boolean enabled = true;
}
