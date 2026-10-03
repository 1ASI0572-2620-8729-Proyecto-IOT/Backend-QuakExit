package com.terraguard.quakexit.subscription.catalog;

import com.terraguard.quakexit.subscription.model.SubscriptionEnums.FeatureCode;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.PlanCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class SubscriptionCatalog {
    private static final List<FeatureCode> ESSENTIAL = List.of(
        FeatureCode.DASHBOARD, FeatureCode.DIGITAL_TWIN, FeatureCode.DEVICE_STATUS,
        FeatureCode.DEVICE_CONTROL_BASIC, FeatureCode.PROPERTY_CONFIGURATION,
        FeatureCode.MANUAL_SIMULATIONS, FeatureCode.PUSH_SMS_ALERTS,
        FeatureCode.EMERGENCY_CONTACTS_5, FeatureCode.LOW_BATTERY_ALERTS,
        FeatureCode.OFFLINE_DEVICE_ALERTS, FeatureCode.EMAIL_SUPPORT);
    private static final List<FeatureCode> PLUS = append(ESSENTIAL,
        FeatureCode.NOTIFICATION_HISTORY, FeatureCode.EARTHQUAKE_REPORTS,
        FeatureCode.SIMULATION_REPORTS, FeatureCode.DEVICE_REPORTS,
        FeatureCode.FALSE_ALARM_REPORTS, FeatureCode.FALSE_ALARMS, FeatureCode.AUDIT);
    private static final List<FeatureCode> BUILDING = append(PLUS,
        FeatureCode.BUILDING_MANAGEMENT, FeatureCode.RESIDENT_MANAGEMENT,
        FeatureCode.BULK_DEVICE_REGISTRATION, FeatureCode.COMMON_AREA_ALARMS,
        FeatureCode.COMMON_AREA_SIMULATIONS, FeatureCode.MAINTENANCE);
    private static final List<FeatureCode> ENTERPRISE = append(BUILDING,
        FeatureCode.MULTI_BUILDING, FeatureCode.UNLIMITED_ADMINISTRATORS,
        FeatureCode.EXTERNAL_API, FeatureCode.IOT_INTEGRATIONS,
        FeatureCode.HISTORICAL_ANALYTICS, FeatureCode.ADVANCED_REPORTS,
        FeatureCode.PRIORITY_SUPPORT);

    private static final Map<PlanCode, PlanDefinition> PLANS = Map.of(
        PlanCode.CLOUD_ESSENTIAL, new PlanDefinition(PlanCode.CLOUD_ESSENTIAL, "Cloud Essential", new BigDecimal("499.00"), new BigDecimal("12.90"), new BigDecimal("129.00"), "PEN", true, ESSENTIAL),
        PlanCode.CLOUD_PLUS, new PlanDefinition(PlanCode.CLOUD_PLUS, "Cloud Plus", new BigDecimal("849.00"), new BigDecimal("24.90"), new BigDecimal("249.00"), "PEN", true, PLUS),
        PlanCode.CLOUD_BUILDING, new PlanDefinition(PlanCode.CLOUD_BUILDING, "Cloud Building", null, new BigDecimal("8.90"), new BigDecimal("90.78"), "PEN", true, BUILDING),
        PlanCode.CLOUD_ENTERPRISE, new PlanDefinition(PlanCode.CLOUD_ENTERPRISE, "Cloud Enterprise", null, null, null, "PEN", true, ENTERPRISE));

    private SubscriptionCatalog() {}
    public static List<PlanDefinition> plans() { return PLANS.values().stream().toList(); }
    public static PlanDefinition require(PlanCode code) { return PLANS.get(code); }
    private static List<FeatureCode> append(List<FeatureCode> base, FeatureCode... extra) { var result = new java.util.ArrayList<>(base); result.addAll(List.of(extra)); return List.copyOf(result); }
}