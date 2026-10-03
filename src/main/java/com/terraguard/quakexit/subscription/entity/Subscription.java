package com.terraguard.quakexit.subscription.entity;

import com.terraguard.quakexit.common.entity.BaseEntity;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.*;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "subscriptions", indexes = @Index(name = "idx_subscription_user_status", columnList = "subscriber_id,status"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Subscription extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "subscriber_id", nullable = false) private User subscriber;
    @Enumerated(EnumType.STRING) @Column(name = "plan_code", nullable = false, length = 30) private PlanCode planCode;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private BillingPeriod billingPeriod;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private SubscriptionStatus status;
    @Enumerated(EnumType.STRING) @Column(name = "scope_type", nullable = false, length = 20) private ScopeType scopeType;
    @Column(name = "scope_id") private Long scopeId;
    @Column(name = "first_year_included", nullable = false) private boolean firstYearIncluded;
    @Column(name = "starts_at", nullable = false) private Instant startsAt;
    @Column(name = "expires_at", nullable = false) private Instant expiresAt;
    @Column(name = "cancelled_at") private Instant cancelledAt;
}