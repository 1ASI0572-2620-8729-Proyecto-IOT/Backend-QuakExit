package com.terraguard.quakexit.subscription.entity;

import com.terraguard.quakexit.common.entity.BaseEntity;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "subscription_orders", uniqueConstraints = @UniqueConstraint(name = "uk_subscription_order_code", columnNames = "order_code"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubscriptionOrder extends BaseEntity {
    @Column(name = "order_code", nullable = false, length = 50) private String orderCode;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "subscription_id", nullable = false) private Subscription subscription;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private PaymentStatus status;
    @Column(precision = 12, scale = 2) private BigDecimal amount;
    @Column(nullable = false, length = 3) private String currency;
    @Enumerated(EnumType.STRING) @Column(name = "billing_period", nullable = false, length = 20) private BillingPeriod billingPeriod;
    @Enumerated(EnumType.STRING) @Column(name = "customer_type", nullable = false, length = 20) private CustomerType customerType;
    @Column(name = "customer_name", length = 160) private String customerName;
    @Column(name = "document_type", length = 20) private String documentType;
    @Column(name = "document_number", length = 30) private String documentNumber;
    @Column(name = "customer_email", nullable = false, length = 180) private String customerEmail;
    @Column(name = "phone_number", length = 20) private String phoneNumber;
    @Column(length = 240) private String address;
    @Column(length = 100) private String city;
    @Column(length = 2) private String country;
    @Column(name = "company_name", length = 180) private String companyName;
    @Column(name = "tax_id", length = 30) private String taxId;
    @Column(name = "building_id") private Long buildingId;
    @Column(name = "department_count") private Integer departmentCount;
    @Column(name = "include_hardware", nullable = false) private boolean includeHardware;
}