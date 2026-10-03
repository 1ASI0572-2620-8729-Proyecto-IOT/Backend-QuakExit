package com.terraguard.quakexit.emergency.controller;

import com.terraguard.quakexit.emergency.dto.ContactDtos.*;
import com.terraguard.quakexit.emergency.service.EmergencyContactService;
import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.FeatureCode;
import com.terraguard.quakexit.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/users/emergency-contacts") @RequiredArgsConstructor @Tag(name = "Contactos de emergencia")
public class EmergencyContactController {
    private final EmergencyContactService service;
    private final UserRepository users;
    private final SubscriptionService subscriptions;
    @PostMapping public ResponseEntity<ContactResponse> add(@Valid @RequestBody ContactRequest request, Authentication authentication) { var user = user(authentication); subscriptions.requireFeature(user, FeatureCode.EMERGENCY_CONTACTS_5); return ResponseEntity.status(HttpStatus.CREATED).body(service.add(request, user)); }
    @GetMapping public ContactList list(Authentication authentication) { var user = user(authentication); subscriptions.requireFeature(user, FeatureCode.EMERGENCY_CONTACTS_5); return service.list(user); }
    private com.terraguard.quakexit.iam.entity.User user(Authentication authentication) { return users.findByEmailIgnoreCase(authentication.getName()).orElseThrow(); }
}
