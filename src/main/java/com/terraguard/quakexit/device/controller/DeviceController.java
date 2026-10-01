package com.terraguard.quakexit.device.controller;

import com.terraguard.quakexit.device.dto.DeviceDtos.*;
import com.terraguard.quakexit.device.service.DeviceService;
import com.terraguard.quakexit.iam.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/devices") @RequiredArgsConstructor
public class DeviceController {
    private final DeviceService service; private final UserRepository users;
    private com.terraguard.quakexit.iam.entity.User user(Authentication a) { return users.findByEmailIgnoreCase(a.getName()).orElseThrow(); }
    @PostMapping("/bind") public ResponseEntity<DeviceResponse> bind(@Valid @RequestBody BindRequest r, Authentication a) { return ResponseEntity.status(HttpStatus.CREATED).body(service.bind(r,user(a))); }
    @GetMapping("/{id}/status") public DeviceResponse status(@PathVariable Long id, Authentication a) { return service.status(id,user(a)); }
    @GetMapping("/battery-status") public java.util.List<BatteryStatusResponse> batteryStatus(Authentication a) { return service.batteryStatus(user(a)); }
    @PostMapping("/{id}/unlock-private") public DeviceResponse unlockPrivate(@PathVariable Long id, Authentication a) { return service.unlockPrivate(id, user(a)); }
    @PutMapping("/{id}/power-mode") public DeviceResponse power(@PathVariable Long id,@Valid @RequestBody PowerModeRequest r,Authentication a) { return service.power(id,r,user(a)); }
}
