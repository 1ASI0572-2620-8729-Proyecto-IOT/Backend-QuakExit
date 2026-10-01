package com.terraguard.quakexit.notification.controller;

import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.notification.dto.NotificationDtos.*;
import com.terraguard.quakexit.notification.service.NotificationManager;
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
    private User user(Authentication authentication) { return users.findByEmailIgnoreCase(authentication.getName()).orElseThrow(); }

    @GetMapping("/preferences") public PreferencesResponse preferences(Authentication authentication) { return manager.preferences(user(authentication)); }
    @PutMapping("/preferences") public PreferencesResponse updatePreferences(@Valid @RequestBody PreferencesRequest request, Authentication authentication) { return manager.updatePreferences(request, user(authentication)); }
    @PostMapping("/push-tokens") public ResponseEntity<PushTokenResponse> addToken(@Valid @RequestBody PushTokenRequest request, Authentication authentication) { return ResponseEntity.status(HttpStatus.CREATED).body(manager.addToken(request, user(authentication))); }
    @DeleteMapping("/push-tokens/{tokenId}") public ResponseEntity<Void> removeToken(@PathVariable Long tokenId, Authentication authentication) { manager.removeToken(tokenId, user(authentication)); return ResponseEntity.noContent().build(); }
    @GetMapping public Page<NotificationResponse> history(@RequestParam(required = false) String channel, @RequestParam(required = false) String status, @RequestParam(required = false) String type, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) { return manager.history(channel, status, type, from, to, pageable); }
}
