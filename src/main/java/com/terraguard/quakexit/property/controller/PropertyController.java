package com.terraguard.quakexit.property.controller;

import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.property.dto.PropertyDtos.*;
import com.terraguard.quakexit.property.service.PropertyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/property") @RequiredArgsConstructor @Tag(name = "Vivienda")
public class PropertyController {
    private final PropertyService service;
    private final UserRepository users;
    @PostMapping("/setup") public LayoutResponse setup(@Valid @RequestBody SetupRequest request, Authentication authentication) { return service.setup(request, user(authentication)); }
    @GetMapping("/layout") public LayoutResponse layout(Authentication authentication) { return service.layout(user(authentication)); }
    private com.terraguard.quakexit.iam.entity.User user(Authentication authentication) { return users.findByEmailIgnoreCase(authentication.getName()).orElseThrow(); }
}
