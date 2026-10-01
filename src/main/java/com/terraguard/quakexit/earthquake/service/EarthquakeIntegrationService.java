package com.terraguard.quakexit.earthquake.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terraguard.quakexit.earthquake.dto.EarthquakeDtos.*;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service @RequiredArgsConstructor
public class EarthquakeIntegrationService {
    private final ObjectMapper mapper;
    private final RestTemplateBuilder restTemplateBuilder;
    public EarthquakeResponse latest() {
        RestTemplate client = restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(3)).setReadTimeout(Duration.ofSeconds(8)).build();
        String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&minlatitude=-19&maxlatitude=0&minlongitude=-82&maxlongitude=-68&minmagnitude=2.5&orderby=time&limit=20";
        try {
            JsonNode features = client.getForObject(url, JsonNode.class).path("features");
            List<EarthquakeItem> items = new ArrayList<>();
            features.forEach(feature -> { JsonNode properties = feature.path("properties"); JsonNode coordinates = feature.path("geometry").path("coordinates"); items.add(new EarthquakeItem(feature.path("id").asText(), Instant.ofEpochMilli(properties.path("time").asLong()), properties.path("mag").asDouble(), properties.path("place").asText(), coordinates.size() > 2 ? coordinates.get(2).asDouble() : null, coordinates.size() > 1 ? coordinates.get(1).asDouble() : null, coordinates.size() > 0 ? coordinates.get(0).asDouble() : null)); });
            return new EarthquakeResponse(items, Instant.now());
        } catch (RuntimeException ex) { throw new IllegalStateException("No se pudo consultar el servicio de sismos", ex); }
    }
}
