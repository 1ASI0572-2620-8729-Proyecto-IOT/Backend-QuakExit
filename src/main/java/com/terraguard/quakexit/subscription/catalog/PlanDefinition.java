package com.terraguard.quakexit.subscription.catalog;

import com.terraguard.quakexit.subscription.model.SubscriptionEnums.FeatureCode;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.PlanCode;
import java.math.BigDecimal;
import java.util.List;

public record PlanDefinition(
    PlanCode code,
    String name,
    BigDecimal hardwarePrice,
    BigDecimal monthlyPrice,
    BigDecimal annualPrice,
    String currency,
    boolean firstYearIncluded,
    List<FeatureCode> features) {}