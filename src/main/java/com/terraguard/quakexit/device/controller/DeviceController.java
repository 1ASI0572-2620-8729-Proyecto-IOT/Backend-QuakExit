package com.terraguard.quakexit.device.controller;

import com.terraguard.quakexit.device.dto.DeviceDtos.*;
import com.terraguard.quakexit.device.service.DeviceService;
import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.FeatureCode;
import com.terraguard.quakexit.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/devices") @RequiredArgsConstructor
public class DeviceController {
    private final DeviceService service; private final UserRepository users; private final SubscriptionService subscriptions;
    private com.terraguard.quakexit.iam.entity.User user(Authentication a) { return users.findByEmailIgnoreCase(a.getName()).orElseThrow(); }
    @PostMapping("/bind") public ResponseEntity<DeviceResponse> bind(@Valid @RequestBody BindRequest r, Authentication a) { var user = user(a); subscriptions.requireFeature(user, FeatureCode.DEVICE_CONTROL_BASIC); return ResponseEntity.status(HttpStatus.CREATED).body(service.bind(r,user)); }
    @GetMapping public java.util.List<DeviceResponse> list(Authentication a) { var user = user(a); subscriptions.requireFeature(user, FeatureCode.DEVICE_STATUS); return service.list(user); }
    @GetMapping("/{id}/status") public DeviceResponse status(@PathVariable Long id, Authentication a) { var user = user(a); subscriptions.requireFeature(user, FeatureCode.DEVICE_STATUS); return service.status(id,user); }
    @GetMapping("/battery-status") public java.util.List<BatteryStatusResponse> batteryStatus(Authentication a) { var user = user(a); subscriptions.requireFeature(user, FeatureCode.DEVICE_STATUS); return service.batteryStatus(user); }
    @PostMapping("/{id}/unlock-private") public DeviceResponse unlockPrivate(@PathVariable Long id, Authentication a) { return service.unlockPrivate(id, user(a)); }
    @PutMapping("/{id}/power-mode") public DeviceResponse power(@PathVariable Long id,@Valid @RequestBody PowerModeRequest r,Authentication a) { var user = user(a); subscriptions.requireFeature(user, FeatureCode.DEVICE_CONTROL_BASIC); return service.power(id,r,user); }
}
