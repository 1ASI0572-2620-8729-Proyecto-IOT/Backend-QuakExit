package com.terraguard.quakexit.subscription.service;

import com.terraguard.quakexit.audit.service.AuditService;
import com.terraguard.quakexit.b2b.entity.Building;
import com.terraguard.quakexit.b2b.repository.BuildingRepository;
import com.terraguard.quakexit.common.exception.ApiExceptions.*;
import com.terraguard.quakexit.common.enums.DomainEnums.Role;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.property.entity.PropertyLayout;
import com.terraguard.quakexit.property.repository.PropertyLayoutRepository;
import com.terraguard.quakexit.subscription.catalog.*;
import com.terraguard.quakexit.subscription.dto.SubscriptionDtos.*;
import com.terraguard.quakexit.subscription.entity.*;
import com.terraguard.quakexit.subscription.exception.FeatureAccessException;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.*;
import com.terraguard.quakexit.subscription.repository.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptions;
    private final SubscriptionOrderRepository orders;
    private final PropertyLayoutRepository layouts;
    private final BuildingRepository buildings;
    private final AuditService audit;
    @Value("${app.payments.simulation-enabled:true}") private boolean simulationEnabled;

    public List<PlanResponse> plans() { return SubscriptionCatalog.plans().stream().map(PlanResponse::from).toList(); }

    @Transactional
    public CurrentResponse current(User user) {
        Subscription subscription = findUsable(user);
        return new CurrentResponse(subscription == null ? null : response(subscription), subscription == null ? List.of() : features(subscription));
    }

    @Transactional
    public List<String> features(User user) {
        Subscription subscription = findUsable(user);
        return subscription == null ? List.of() : features(subscription);
    }

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request, User user) {
        PlanDefinition plan = SubscriptionCatalog.require(request.planCode());
        validateCustomer(request, user);
        validateScope(request, user);
        if (findUsable(user) != null) throw new BusinessRuleException("Ya existe una suscripcion activa; use renovacion");
        boolean firstSubscription = subscriptions.findTopBySubscriberIdOrderByCreatedAtDesc(user.getId()).isEmpty();
        return createOrder(request, user, plan, firstSubscription);
    }

    @Transactional
    public CheckoutResponse renew(User user) {
        Subscription previous = subscriptions.findTopBySubscriberIdOrderByCreatedAtDesc(user.getId()).orElseThrow(() -> new ResourceNotFoundException("No existe una suscripcion para renovar"));
        if (previous.getStatus() == SubscriptionStatus.ACTIVE || previous.getStatus() == SubscriptionStatus.TRIAL) throw new BusinessRuleException("La suscripcion aun esta activa");
        CheckoutRequest request = new CheckoutRequest(previous.getPlanCode(), previous.getBillingPeriod(), CustomerType.PERSON, previous.getScopeType(), previous.getScopeType() == ScopeType.PROPERTY ? previous.getScopeId() : null, user.getFullName(), null, null, user.getEmail(), user.getPhoneNumber(), null, null, "PE", null, null, previous.getScopeType() == ScopeType.BUILDING ? previous.getScopeId() : null, null, false, true);
        return createOrder(request, user, SubscriptionCatalog.require(previous.getPlanCode()), false);
    }

    @Transactional
    public PaymentResponse simulatePayment(PaymentRequest request, User user) {
        if (!simulationEnabled) throw new BusinessRuleException("La simulacion de pagos esta deshabilitada");
        return processPayment(request, user);
    }

    @Transactional
    public PaymentResponse webhook(PaymentRequest request, User user) { return processPayment(request, user); }

    @Transactional
    public CancelResponse cancel(User user) {
        Subscription subscription = findUsable(user);
        if (subscription == null) throw new ResourceNotFoundException("No existe una suscripcion activa");
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(Instant.now());
        subscriptions.save(subscription);
        audit.record(user, "SUBSCRIPTION_CANCELLED", "SUBSCRIPTION", subscription.getId(), null, null, java.util.Map.of("planCode", subscription.getPlanCode().name()), null);
        return new CancelResponse("sub-" + subscription.getId(), subscription.getStatus(), subscription.getCancelledAt());
    }

    @Transactional(readOnly = true)
    public void requireFeature(User user, FeatureCode feature) {
        if (user.getRole() == Role.SYSTEM_ADMIN) {
            return;
        }
        Subscription subscription = findUsable(user);
        if (subscription == null || !features(subscription).contains(feature.name())) {
            throw new FeatureAccessException("FEATURE_NOT_INCLUDED", "Esta funcion requiere una suscripcion que incluya " + feature.name() + ".");
        }
    }

    private CheckoutResponse createOrder(CheckoutRequest request, User user, PlanDefinition plan, boolean firstSubscription) {
        BigDecimal cloudAmount = cloudAmount(plan, request.billingPeriod(), request.departmentCount(), firstSubscription && plan.firstYearIncluded());
        BigDecimal hardwareAmount = Boolean.TRUE.equals(request.includeHardware()) ? plan.hardwarePrice() : BigDecimal.ZERO;
        BigDecimal amount = cloudAmount == null || hardwareAmount == null ? null : cloudAmount.add(hardwareAmount);
        Subscription subscription = subscriptions.save(Subscription.builder().subscriber(user).planCode(plan.code()).billingPeriod(request.billingPeriod()).status(SubscriptionStatus.PENDING_PAYMENT).scopeType(request.scopeType() == null ? ScopeType.USER : request.scopeType()).scopeId(scopeId(request)).firstYearIncluded(firstSubscription && plan.firstYearIncluded()).startsAt(Instant.now()).expiresAt(Instant.now()).build());
        SubscriptionOrder order = orders.save(SubscriptionOrder.builder().orderCode("order-" + UUID.randomUUID().toString().substring(0, 8)).subscription(subscription).status(PaymentStatus.PENDING).amount(amount).currency(plan.currency()).billingPeriod(request.billingPeriod()).customerType(request.customerType()).customerName(request.fullName()).documentType(request.documentType()).documentNumber(request.documentNumber()).customerEmail(request.email() == null ? user.getEmail() : request.email()).phoneNumber(request.phoneNumber()).address(request.address()).city(request.city()).country(request.country()).companyName(request.companyName()).taxId(request.taxId()).buildingId(request.buildingId()).departmentCount(request.departmentCount()).includeHardware(Boolean.TRUE.equals(request.includeHardware())).build());
        return checkoutResponse(order, cloudAmount, hardwareAmount);
    }

    private PaymentResponse processPayment(PaymentRequest request, User user) {
        SubscriptionOrder order = orders.findByOrderCode(request.orderId()).orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));
        if (!order.getSubscription().getSubscriber().getId().equals(user.getId())) throw new org.springframework.security.access.AccessDeniedException("Orden ajena");
        if (order.getStatus() != PaymentStatus.PENDING) throw new BusinessRuleException("La orden ya fue procesada");
        Subscription subscription = order.getSubscription();
        if (request.result() == PaymentResult.APPROVED) {
            order.setStatus(PaymentStatus.APPROVED);
            Instant startsAt = Instant.now();
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setStartsAt(startsAt);
            subscription.setExpiresAt(startsAt.plus(subscription.isFirstYearIncluded() ? 365 : subscription.getBillingPeriod() == BillingPeriod.MONTHLY ? 30 : 365, ChronoUnit.DAYS));
            subscriptions.save(subscription);
        } else if (request.result() == PaymentResult.REJECTED) {
            order.setStatus(PaymentStatus.REJECTED);
            subscription.setStatus(SubscriptionStatus.PAST_DUE);
            subscriptions.save(subscription);
        }
        orders.save(order);
        return new PaymentResponse(order.getOrderCode(), "sub-" + subscription.getId(), subscription.getStatus(), subscription.getPlanCode().name(), subscription.getStartsAt(), subscription.getExpiresAt());
    }

    private Subscription findUsable(User user) {
        Subscription subscription = subscriptions.findTopBySubscriberIdAndStatusInOrderByCreatedAtDesc(user.getId(), List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL)).orElse(null);
        if (subscription != null && subscription.getExpiresAt().isBefore(Instant.now())) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptions.save(subscription);
            return null;
        }
        return subscription;
    }

    private List<String> features(Subscription subscription) { return SubscriptionCatalog.require(subscription.getPlanCode()).features().stream().map(Enum::name).toList(); }
    private SubscriptionResponse response(Subscription subscription) { return new SubscriptionResponse("sub-" + subscription.getId(), subscription.getPlanCode().name(), subscription.getStatus(), subscription.getBillingPeriod(), subscription.getScopeType(), subscription.getScopeId(), subscription.isFirstYearIncluded(), subscription.getStartsAt(), subscription.getExpiresAt()); }
    private CheckoutResponse checkoutResponse(SubscriptionOrder order, BigDecimal cloudAmount, BigDecimal hardwareAmount) { return new CheckoutResponse(order.getOrderCode(), "sub-" + order.getSubscription().getId(), order.getSubscription().getStatus(), order.getAmount(), hardwareAmount, cloudAmount, order.getCurrency(), order.getBillingPeriod(), order.getSubscription().getPlanCode().name()); }
    private BigDecimal cloudAmount(PlanDefinition plan, BillingPeriod period, Integer departmentCount, boolean included) { if (included) return BigDecimal.ZERO; if (plan.monthlyPrice() == null) return null; BigDecimal amount = period == BillingPeriod.MONTHLY ? plan.monthlyPrice() : plan.annualPrice(); return plan.code() == PlanCode.CLOUD_BUILDING ? amount.multiply(BigDecimal.valueOf(departmentCount == null ? 0 : departmentCount)) : amount; }
    private Long scopeId(CheckoutRequest request) { return request.scopeType() == ScopeType.PROPERTY ? request.propertyId() : request.scopeType() == ScopeType.BUILDING ? request.buildingId() : null; }

    private void validateCustomer(CheckoutRequest request, User user) {
        if (request.email() == null && user.getEmail() == null) throw new BusinessRuleException("El email es obligatorio");
        if (request.customerType() == CustomerType.COMPANY && (isBlank(request.companyName()) || isBlank(request.taxId()))) throw new BusinessRuleException("Las empresas requieren companyName y taxId");
        if (request.planCode() == PlanCode.CLOUD_BUILDING && (request.departmentCount() == null || request.departmentCount() < 1)) throw new BusinessRuleException("Cloud Building requiere departmentCount");
    }

    private void validateScope(CheckoutRequest request, User user) {
        ScopeType scope = request.scopeType() == null ? ScopeType.USER : request.scopeType();
        if (scope == ScopeType.PROPERTY) {
            PropertyLayout layout = layouts.findById(request.propertyId()).orElseThrow(() -> new ResourceNotFoundException("Vivienda no encontrada"));
            if (!layout.getOwner().getId().equals(user.getId())) throw new org.springframework.security.access.AccessDeniedException("Vivienda ajena");
        }
        if (scope == ScopeType.BUILDING) {
            Building building = buildings.findById(request.buildingId()).orElseThrow(() -> new ResourceNotFoundException("Edificio no encontrado"));
            if (!building.getOwner().getId().equals(user.getId())) throw new org.springframework.security.access.AccessDeniedException("Edificio ajeno");
        }
    }

    private boolean isBlank(String value) { return value == null || value.isBlank(); }
}