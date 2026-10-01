package com.terraguard.quakexit.b2b.controller;

import com.terraguard.quakexit.b2b.dto.B2bDtos.*;
import com.terraguard.quakexit.b2b.service.B2bDeviceService;
import com.terraguard.quakexit.iam.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController @RequestMapping("/api/v1/b2b") @RequiredArgsConstructor @Tag(name = "B2B")
public class B2bController {
    private final B2bDeviceService service;
    private final UserRepository users;
    @PostMapping("/devices/bulk-register") public ResponseEntity<BulkRegisterResponse> bulkRegister(@Valid @RequestBody BulkRegisterRequest request, Authentication authentication) { return ResponseEntity.status(HttpStatus.CREATED).body(service.bulkRegister(request, user(authentication))); }
    @PostMapping(value = "/devices/bulk-register-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<BulkRegisterResponse> bulkRegisterCsv(@RequestParam Long buildingId, @RequestPart("file") MultipartFile file, Authentication authentication) { return ResponseEntity.status(HttpStatus.CREATED).body(service.bulkRegisterCsv(buildingId, file, user(authentication))); }
    @PostMapping("/alarms/trigger-all") public AlarmResponse triggerAll(@RequestParam Long buildingId, Authentication authentication) { return service.triggerAll(buildingId, user(authentication)); }
    private com.terraguard.quakexit.iam.entity.User user(Authentication authentication) { return users.findByEmailIgnoreCase(authentication.getName()).orElseThrow(); }
}
