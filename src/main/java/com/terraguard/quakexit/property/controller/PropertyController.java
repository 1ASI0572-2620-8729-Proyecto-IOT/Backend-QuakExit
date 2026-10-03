package com.terraguard.quakexit.property.controller;

import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.property.dto.PropertyDtos.*;
import com.terraguard.quakexit.property.service.PropertyService;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.FeatureCode;
import com.terraguard.quakexit.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/property") @RequiredArgsConstructor @Tag(name = "Vivienda")
public class PropertyController {
    private final PropertyService service;
    private final UserRepository users;
    private final SubscriptionService subscriptions;
    @PostMapping("/setup") public LayoutResponse setup(@Valid @RequestBody SetupRequest request, Authentication authentication) { var user = user(authentication); subscriptions.requireFeature(user, FeatureCode.PROPERTY_CONFIGURATION); return service.setup(request, user); }
    @GetMapping("/layout") public LayoutResponse layout(Authentication authentication) { var user = user(authentication); subscriptions.requireFeature(user, FeatureCode.PROPERTY_CONFIGURATION); return service.layout(user); }
    private com.terraguard.quakexit.iam.entity.User user(Authentication authentication) { return users.findByEmailIgnoreCase(authentication.getName()).orElseThrow(); }
}
