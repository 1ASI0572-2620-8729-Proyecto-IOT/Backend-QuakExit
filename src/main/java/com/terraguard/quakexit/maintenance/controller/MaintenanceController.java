package com.terraguard.quakexit.maintenance.controller;

import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.maintenance.dto.MaintenanceDtos.*;
import com.terraguard.quakexit.maintenance.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/maintenance-alerts") @RequiredArgsConstructor
public class MaintenanceController {
    private final MaintenanceService service; private final UserRepository users;
    private User user(Authentication a) { return users.findByEmailIgnoreCase(a.getName()).orElseThrow(); }
    @GetMapping public Page<MaintenanceResponse> list(@RequestParam(required = false) String status, @RequestParam(required = false) String type, @RequestParam(required = false) Long deviceId, @RequestParam(required = false) Long buildingId, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) { return service.list(status, type, deviceId, buildingId, pageable); }
    @PatchMapping("/{id}/acknowledge") public MaintenanceResponse acknowledge(@PathVariable Long id, Authentication authentication) { return service.acknowledge(id, user(authentication)); }
    @PatchMapping("/{id}/resolve") public MaintenanceResponse resolve(@PathVariable Long id, @Valid @RequestBody(required = false) ResolutionRequest request, Authentication authentication) { return service.resolve(id, request, user(authentication)); }
}
