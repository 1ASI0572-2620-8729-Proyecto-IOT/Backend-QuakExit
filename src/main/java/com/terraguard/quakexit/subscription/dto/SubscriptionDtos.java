package com.terraguard.quakexit.subscription.dto;

import com.terraguard.quakexit.subscription.catalog.PlanDefinition;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class SubscriptionDtos {
    private SubscriptionDtos() {}

    public record PlanResponse(String code, String name, BigDecimal hardwarePrice, BigDecimal monthlyPrice, BigDecimal annualPrice, String currency, boolean firstYearIncluded, List<String> features) {
        public static PlanResponse from(PlanDefinition plan) { return new PlanResponse(plan.code().name(), plan.name(), plan.hardwarePrice(), plan.monthlyPrice(), plan.annualPrice(), plan.currency(), plan.firstYearIncluded(), plan.features().stream().map(Enum::name).toList()); }
    }
    public record SubscriptionResponse(String subscriptionId, String planCode, SubscriptionStatus status, BillingPeriod billingPeriod, ScopeType scopeType, Long scopeId, boolean firstYearIncluded, Instant startsAt, Instant expiresAt) {}
    public record CurrentResponse(SubscriptionResponse subscription, List<String> features) {}
    public record CheckoutRequest(
        @NotNull PlanCode planCode,
        @NotNull BillingPeriod billingPeriod,
        @NotNull CustomerType customerType,
        ScopeType scopeType,
        @Positive Long propertyId,
        @Size(max = 160) String fullName,
        @Size(max = 20) String documentType,
        @Size(max = 30) String documentNumber,
        @Email @Size(max = 180) String email,
        @Size(max = 20) String phoneNumber,
        @Size(max = 240) String address,
        @Size(max = 100) String city,
        @Size(min = 2, max = 2) String country,
        @Size(max = 180) String companyName,
        @Size(max = 30) String taxId,
        @Positive Long buildingId,
        @Positive Integer departmentCount,
        Boolean includeHardware,
        @AssertTrue(message = "Debe aceptar los terminos") boolean acceptTerms) {}
    public record CheckoutResponse(String orderId, String subscriptionId, SubscriptionStatus status, BigDecimal amount, BigDecimal hardwareAmount, BigDecimal cloudAmount, String currency, BillingPeriod billingPeriod, String planCode) {}
    public record PaymentRequest(@NotBlank String orderId, @NotNull PaymentResult result) {}
    public enum PaymentResult { APPROVED, REJECTED, PENDING }
    public record PaymentResponse(String orderId, String subscriptionId, SubscriptionStatus status, String planCode, Instant startsAt, Instant expiresAt) {}
    public record CancelResponse(String subscriptionId, SubscriptionStatus status, Instant cancelledAt) {}
}