package com.terraguard.quakexit.audit.controller;

import com.terraguard.quakexit.audit.dto.AuditDtos.AuditResponse;
import com.terraguard.quakexit.audit.service.AuditService;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.FeatureCode;
import com.terraguard.quakexit.subscription.service.SubscriptionService;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.iam.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/auditoria") @RequiredArgsConstructor @Tag(name = "Auditoria")
public class AuditController {
    private final AuditService service;
    private final UserRepository users;
    private final SubscriptionService subscriptions;

    @GetMapping
    public Page<AuditResponse> search(@RequestParam(required = false) Long userId,
                                      @RequestParam(required = false) String action,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
                                      @RequestParam(required = false) Long propertyId,
                                      @RequestParam(required = false) Long buildingId,
                                      @RequestParam(required = false) String entityType,
                                      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                      Authentication authentication) {
        User user = users.findByEmailIgnoreCase(authentication.getName()).orElseThrow();
        subscriptions.requireFeature(user, FeatureCode.AUDIT);
        if (user.getRole().name().equals("HOMEOWNER")) throw new AccessDeniedException("Sin permisos para consultar auditoria");
        return service.search(userId, action, from, to, propertyId, buildingId, entityType, pageable);
    }
}
