package com.terraguard.quakexit.earthquake.controller;

import com.terraguard.quakexit.earthquake.dto.EarthquakeDtos.EarthquakeResponse;
import com.terraguard.quakexit.earthquake.service.EarthquakeIntegrationService;
import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.FeatureCode;
import com.terraguard.quakexit.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController @RequestMapping("/api/v1/earthquakes") @RequiredArgsConstructor @Tag(name = "Sismos")
public class EarthquakeController {
    private final EarthquakeIntegrationService service;
    private final UserRepository users;
    private final SubscriptionService subscriptions;
    @GetMapping("/realtime") public EarthquakeResponse realtime(Authentication authentication) { var user = users.findByEmailIgnoreCase(authentication.getName()).orElseThrow(); subscriptions.requireFeature(user, FeatureCode.DASHBOARD); return service.latest(); }
}
