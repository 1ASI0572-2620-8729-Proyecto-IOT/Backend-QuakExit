package com.terraguard.quakexit.emergency.controller;

import com.terraguard.quakexit.emergency.dto.EmergencyDtos.*;
import com.terraguard.quakexit.emergency.service.SeismicService;
import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.FeatureCode;
import com.terraguard.quakexit.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1") @RequiredArgsConstructor
public class EmergencyController {
    private final SeismicService service; private final UserRepository users; private final SubscriptionService subscriptions;
    private com.terraguard.quakexit.iam.entity.User user(Authentication a) { return users.findByEmailIgnoreCase(a.getName()).orElseThrow(); }
    @PostMapping("/internal/mqtt/simulate-reading") public ResponseEntity<EventResponse> reading(@Valid @RequestBody ReadingRequest r) { return ResponseEntity.ok(service.process(r)); }
    @PostMapping("/emergencies/{eventId}/false-alarm") public EventResponse falseAlarm(@PathVariable Long eventId,@Valid @RequestBody FalseAlarmRequest r,Authentication a) { var user = user(a); subscriptions.requireFeature(user, FeatureCode.FALSE_ALARMS); return service.falseAlarm(eventId,r.reason(),user); }
}
