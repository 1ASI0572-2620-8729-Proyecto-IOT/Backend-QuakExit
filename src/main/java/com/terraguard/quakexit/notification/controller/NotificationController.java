package com.terraguard.quakexit.notification.controller;

import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.notification.dto.NotificationDtos.*;
import com.terraguard.quakexit.notification.service.NotificationManager;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.FeatureCode;
import com.terraguard.quakexit.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/notificaciones") @RequiredArgsConstructor
public class NotificationController {
    private final NotificationManager manager;
    private final UserRepository users;
    private final SubscriptionService subscriptions;
    private User user(Authentication authentication) { return users.findByEmailIgnoreCase(authentication.getName()).orElseThrow(); }

    @GetMapping("/preferences") public PreferencesResponse preferences(Authentication authentication) { var user = user(authentication); subscriptions.requireFeature(user, FeatureCode.PUSH_SMS_ALERTS); return manager.preferences(user); }
    @PutMapping("/preferences") public PreferencesResponse updatePreferences(@Valid @RequestBody PreferencesRequest request, Authentication authentication) { var user = user(authentication); subscriptions.requireFeature(user, FeatureCode.PUSH_SMS_ALERTS); return manager.updatePreferences(request, user); }
    @PostMapping("/push-tokens") public ResponseEntity<PushTokenResponse> addToken(@Valid @RequestBody PushTokenRequest request, Authentication authentication) { var user = user(authentication); subscriptions.requireFeature(user, FeatureCode.PUSH_SMS_ALERTS); return ResponseEntity.status(HttpStatus.CREATED).body(manager.addToken(request, user)); }
    @DeleteMapping("/push-tokens/{tokenId}") public ResponseEntity<Void> removeToken(@PathVariable Long tokenId, Authentication authentication) { var user = user(authentication); subscriptions.requireFeature(user, FeatureCode.PUSH_SMS_ALERTS); manager.removeToken(tokenId, user); return ResponseEntity.noContent().build(); }
    @GetMapping public Page<NotificationResponse> history(@RequestParam(required = false) String channel, @RequestParam(required = false) String status, @RequestParam(required = false) String type, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) { var user = user(authentication); subscriptions.requireFeature(user, FeatureCode.NOTIFICATION_HISTORY); return manager.history(channel, status, type, from, to, pageable); }
}
