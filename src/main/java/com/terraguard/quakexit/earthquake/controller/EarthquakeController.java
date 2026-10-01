package com.terraguard.quakexit.earthquake.controller;

import com.terraguard.quakexit.earthquake.dto.EarthquakeDtos.EarthquakeResponse;
import com.terraguard.quakexit.earthquake.service.EarthquakeIntegrationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/earthquakes") @RequiredArgsConstructor @Tag(name = "Sismos")
public class EarthquakeController {
    private final EarthquakeIntegrationService service;
    @GetMapping("/realtime") public EarthquakeResponse realtime() { return service.latest(); }
}
