package com.terraguard.quakexit.report.controller;

import com.terraguard.quakexit.report.service.ReportService;
import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.FeatureCode;
import com.terraguard.quakexit.subscription.service.SubscriptionService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/reportes") @RequiredArgsConstructor
public class ReportController {
    private final ReportService reports;
    private final UserRepository users;
    private final SubscriptionService subscriptions;
    @GetMapping("/sismos") public Page<?> earthquakes(@RequestParam(required = false) Long propertyId, @RequestParam(required = false) Long buildingId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to, @RequestParam(required = false) String status, @RequestParam(required = false) Double minMagnitude, @RequestParam(required = false) Double maxMagnitude, @PageableDefault(size = 20, sort = "detectedAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) { subscriptions.requireFeature(user(authentication), FeatureCode.EARTHQUAKE_REPORTS); return reports.earthquakes(propertyId, buildingId, from, to, status, minMagnitude, maxMagnitude, pageable); }
    @GetMapping("/simulacros") public Page<?> simulations(@RequestParam(required = false) Long buildingId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to, @RequestParam(required = false) String status, @PageableDefault(size = 20, sort = "startedAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) { subscriptions.requireFeature(user(authentication), FeatureCode.SIMULATION_REPORTS); return reports.simulations(buildingId, from, to, status, pageable); }
    @GetMapping("/dispositivos") public Page<?> devices(@RequestParam(required = false) Long buildingId, @RequestParam(required = false) String status, @RequestParam(required = false) String powerMode, @RequestParam(required = false) Boolean lowBattery, @RequestParam(required = false) Boolean disconnected, @PageableDefault(size = 20, sort = "lastSeenAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) { subscriptions.requireFeature(user(authentication), FeatureCode.DEVICE_REPORTS); return reports.devices(buildingId, status, powerMode, lowBattery, disconnected, pageable); }
    @GetMapping("/falsas-alarmas") public Page<?> falseAlarms(@RequestParam(required = false) Long buildingId, @RequestParam(required = false) Long deviceId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to, @PageableDefault(size = 20, sort = "detectedAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) { subscriptions.requireFeature(user(authentication), FeatureCode.FALSE_ALARM_REPORTS); return reports.falseAlarms(buildingId, deviceId, from, to, pageable); }
    @GetMapping("/resumen") public java.util.Map<String, Object> summary(@RequestParam(required = false) Long buildingId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to, Authentication authentication) { subscriptions.requireFeature(user(authentication), FeatureCode.EARTHQUAKE_REPORTS); return reports.summary(buildingId, from, to); }
    private com.terraguard.quakexit.iam.entity.User user(Authentication authentication) { return users.findByEmailIgnoreCase(authentication.getName()).orElseThrow(); }
}
