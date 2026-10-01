package com.terraguard.quakexit.simulation.controller;

import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.simulation.dto.SimulationDtos.*;
import com.terraguard.quakexit.simulation.service.SimulationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/simulations") @RequiredArgsConstructor @Tag(name = "Simulacros")
public class SimulationController {
    private final SimulationService service;
    private final UserRepository users;
    @PostMapping("/trigger") public TriggerResponse trigger(@Valid @RequestBody TriggerRequest request, Authentication authentication) { return service.trigger(request, users.findByEmailIgnoreCase(authentication.getName()).orElseThrow()); }
}
